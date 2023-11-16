package com.akvelon.awstest;

import com.akvelon.awstest.model.Image;
import com.akvelon.awstest.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/images")
public class ImagesController {
    @Autowired
    private ImageService service;

    @Operation(summary = "Saves file in S3")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Image saved")})
    @RequestMapping(
            path = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = "application/json")
    public ResponseEntity<Image> uploadImage(
            @RequestPart(value = "image") @Schema(type = "string", format = "binary") MultipartFile file) {
        try {
            // Assuming service.uploadPhoto(file) returns a valid Image object
            Image image = service.uploadPhoto(file);

            return new ResponseEntity<>(image, HttpStatus.OK);
        } catch (Exception e) {
            // Handle exceptions and return an appropriate ResponseEntity
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
