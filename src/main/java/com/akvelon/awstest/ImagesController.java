package com.akvelon.awstest;

import com.akvelon.awstest.service.S3ClientService;
import com.amazonaws.services.s3.model.PutObjectResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/images")
public class ImagesController {
    @Autowired
    private S3ClientService service;

    @Operation(summary = "Saves file in S3")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Image saved")})
    @RequestMapping(
            path = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestPart(value = "image") @Schema(type = "string", format = "binary") MultipartFile file,
                                              @RequestParam("someId") Long someId) throws IOException {

        PutObjectResult putObjectResult = service.uploadPhoto(file);
        if (putObjectResult == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(putObjectResult.getMetadata().toString());
    }
}
