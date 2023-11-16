package com.akvelon.awstest.service;

import com.akvelon.awstest.dao.ImageRepository;
import com.akvelon.awstest.model.Image;
import com.amazonaws.services.s3.model.PutObjectResult;
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
    @Autowired
    private ImageRepository imageRepository;

    //@Transactional
    public Long uploadPhoto(MultipartFile file) throws IOException {
        Image oldImage = imageRepository.findByName(file.getOriginalFilename());
        boolean isExistInS3 = service.isObjectExists(file.getOriginalFilename());
        // already exists
        if (oldImage != null && isExistInS3) {
            return oldImage.getId();
        }


        Image image = imageRepository.save(new Image(file.getOriginalFilename()));
        // not exists
        PutObjectResult putObjectResult = service.uploadPhoto(file, image.getId());
        if (putObjectResult == null) {
            throw new NullPointerException();
        }

        return image.getId();
    }
}
