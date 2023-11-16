package com.akvelon.awstest.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class S3ClientService {
    private final Regions clientRegion = Regions.US_EAST_1;
    private final String bucketName = "dudko-aws-test-bucket";
    private final AmazonS3 s3Client;

    public S3ClientService() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion(clientRegion)
                .build();
    }

    public PutObjectResult uploadPhoto(MultipartFile file, Long id) throws IOException {
        // Upload a file as a new object with ContentType and title specified.
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("image/png");
        metadata.addUserMetadata("id", id.toString());
        metadata.addUserMetadata("name", file.getOriginalFilename());
        metadata.addUserMetadata("size", String.valueOf(file.getSize()));
        PutObjectRequest request = new PutObjectRequest(bucketName, file.getOriginalFilename(), file.getInputStream(), metadata);

        return s3Client.putObject(request);
    }

    public boolean isObjectExists(String originalFilename) {
        return s3Client.doesObjectExist(bucketName, originalFilename);
    }

}
