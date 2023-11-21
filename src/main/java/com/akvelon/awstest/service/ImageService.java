package com.akvelon.awstest.service;

import com.akvelon.awstest.model.Image;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@EnableAsync
public class ImageService {
    @Autowired
    private S3ClientService service;
    @Autowired
    private DynamoDbService dynamoDbService;
    @Autowired
    private SQSService sqsService;

    @Transactional
    public Image uploadPhoto(MultipartFile file) throws IOException {
        Long id = System.currentTimeMillis();
        Image image = service.uploadPhoto(file, id, "original");
        dynamoDbService.saveTaskState(image,
                id.toString(),
                "Created");
        sqsService.putTaskInSQS(id.toString());

        /*sqsService.setMessageReceivedListener(message -> dynamoDbService.updateTaskState(message,
                "InProgress"));*/

        /*MultipartFile file1 = rotate(file);
        Image image1 = service.uploadPhoto(file1, id, "processed");
        dynamoDbService.updateTaskState(id.toString(),
                "Done");*/

        /*
        dynamoDbService.updateTaskState(id.toString(),
                "processed/" + file.getOriginalFilename(),
                "InProgress");*/


        return image;
    }

    private MultipartFile rotate(InputStream fileStream, String name) throws IOException {
        BufferedImage originalImage = ImageIO.read(fileStream);

        // Rotate the image (adjust the angle as needed)
        BufferedImage rotatedImage = rotateImage(originalImage, 180);

        // Save the rotated image to a temporary file
        Path tempFile = Files.createTempFile("rotated_", ".png");
        ImageIO.write(rotatedImage, "png", tempFile.toFile());

        // Create a new MultipartFile from the temporary file
        MultipartFile rotatedMultipartFile = new MockMultipartFile(
                "file",               // parameter name in the form
                name,
                MediaType.IMAGE_PNG_VALUE,
                Files.newInputStream(tempFile));

        // Clean up the temporary file
        Files.deleteIfExists(tempFile);

        return rotatedMultipartFile;
    }

    private BufferedImage rotateImage(BufferedImage originalImage, double angle) {
        // Perform image rotation
        double radians = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int width = originalImage.getWidth();
        int height = originalImage.getHeight();
        int newWidth = (int) Math.floor(width * cos + height * sin);
        int newHeight = (int) Math.floor(height * cos + width * sin);

        BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, originalImage.getType());
        Graphics2D g = rotatedImage.createGraphics();

        AffineTransform transform = new AffineTransform();
        transform.translate((double) (newWidth - width) / 2, (double) (newHeight - height) / 2);
        transform.rotate(radians, (double) width / 2, (double) height / 2);

        g.setTransform(transform);
        g.drawImage(originalImage, 0, 0, null);

        g.dispose();

        return rotatedImage;
    }

    public String getTask(String id) {
        return dynamoDbService.getTaskStateById(id);
    }

    public void processImage() throws IOException {
        sqsService.receiveMessages(messageBody -> {
            dynamoDbService.updateTaskState(messageBody, "InProgress");
            S3Object s3Object = service.getObject("original/" + messageBody);
            MultipartFile multipartFile = rotate(s3Object.getObjectContent(), "processed/" + messageBody);
            service.uploadPhoto(multipartFile, Long.valueOf(messageBody), "processed");
            dynamoDbService.updateTaskState(messageBody, "Done");
        });
    }

    private static BufferedImage convertInputStreamToBufferedImage(InputStream inputStream) throws IOException {
        return ImageIO.read(inputStream);
    }
}
