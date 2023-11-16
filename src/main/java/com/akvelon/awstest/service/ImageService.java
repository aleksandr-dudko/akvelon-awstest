package com.akvelon.awstest.service;

import com.akvelon.awstest.model.Image;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@EnableAsync
public class ImageService {
    @Autowired
    private S3ClientService service;

    @Transactional
    public Image uploadPhoto(MultipartFile file) throws IOException {
        boolean isExistInS3 = service.isObjectExists(file.getOriginalFilename());
        // already exists
        if (isExistInS3) {
            S3Object s3Object = service.getObject(file.getOriginalFilename());
            Long id = Long.valueOf(s3Object.getObjectMetadata().getUserMetadata().get("id"));
            return new Image(id, file.getOriginalFilename());
        }

        // not exists
        Long id = System.currentTimeMillis();
        PutObjectResult putObjectResult = service.uploadPhoto(file, id);
        if (putObjectResult == null) {
            throw new NullPointerException();
        }

        return new Image(id, file.getOriginalFilename());
    }
}
