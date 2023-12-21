package com.akvelon.awstest.service;

import com.akvelon.awstest.model.ImageData;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
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

import static com.akvelon.awstest.config.AWSSettings.*;

@Service
@EnableAsync
public class ImageService {
    private S3ClientService service;
    private DynamoDbService dynamoDbService;
    private SQSService sqsService;

    @Transactional
    public ImageData uploadPhoto(MultipartFile file) throws IOException {
        Long id = System.currentTimeMillis();
        ImageData imageData = service.uploadPhoto(file, id, ORIGINAL);
        dynamoDbService.saveTaskState(imageData, id.toString(), CREATED_STATE);
        sqsService.putTaskInSQS(id.toString());

        return imageData;
    }

    private MultipartFile rotate(InputStream fileStream, String name) throws IOException {
        BufferedImage originalImage = ImageIO.read(fileStream);

        // Rotate the image (adjust the angle as needed)
        BufferedImage rotatedImage = rotateImage(originalImage, ROTATION_ANGLE);

        Path tempFile = Files.createTempFile(FILE_PREFIX, FILE_SUFFIX);
        ImageIO.write(rotatedImage, "png", tempFile.toFile());

        MultipartFile rotatedMultipartFile = new MockMultipartFile(
                PARAMETER_NAME,
                name,
                MEDIA_TYPE,
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

    @Transactional
    public String getTaskState(String id) {
        return dynamoDbService.getTaskStateById(id);
    }

    public void processImage() throws IOException {
        sqsService.receiveMessages(messageBody -> {
            // Update task state to "InProgress"
            dynamoDbService.updateTaskState(messageBody, IN_PROGRESS_STATE);

            // Retrieve the original image from S3
            S3Object s3Object = service.getObject(ORIGINAL + "/" + messageBody);

            // Rotate the image
            MultipartFile multipartFile = rotate(s3Object.getObjectContent(), PROCESSED_FOLDER + "/" + messageBody);

            // Upload the processed image to S3
            service.uploadPhoto(multipartFile, Long.valueOf(messageBody), PROCESSED_FOLDER);

            // Update task state to "Done"
            dynamoDbService.updateTaskState(messageBody, DONE_STATE);
        });
    }
}
