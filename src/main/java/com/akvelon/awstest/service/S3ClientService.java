package com.akvelon.awstest.service;

import com.akvelon.awstest.model.ImageData;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.akvelon.awstest.config.AWSSettings.BUCKET_NAME;
import static com.akvelon.awstest.config.AWSSettings.CLIENT_REGION;
import static com.akvelon.awstest.config.Constants.*;

@Service
@Transactional
public class S3ClientService {
    private final AmazonS3 s3Client;

    public S3ClientService() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(CLIENT_REGION)
                .build();
    }

    public ImageData uploadPhoto(MultipartFile file, Long id, String folder) throws IOException {
        // Upload a file as a new object with ContentType and title specified.
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(CONTENT_TYPE_IMAGE_PNG);
        metadata.addUserMetadata(USER_METADATA_ID, id.toString());
        metadata.addUserMetadata(USER_METADATA_NAME, file.getOriginalFilename());
        metadata.addUserMetadata(USER_METADATA_SIZE, String.valueOf(file.getSize()));
        PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, folder + "/" + id, file.getInputStream(), metadata);
        s3Client.putObject(request);

        return new ImageData(id, BUCKET_NAME, file.getOriginalFilename());
    }

    public boolean isObjectExists(String originalFilename) {
        return s3Client.doesObjectExist(BUCKET_NAME, originalFilename);
    }

    public S3Object getObject(String originalFilename) {
        GetObjectRequest getObjectRequest = new GetObjectRequest(BUCKET_NAME, originalFilename);

        return s3Client.getObject(getObjectRequest);
    }
}
