package com.akvelon.awstest.controller;

import com.akvelon.awstest.model.ImageData;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@Controller
@RequestMapping("/images")
public class ImagesController {
    private ImageService service;

    @Operation(summary = "Uploads an image to S3")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image successfully saved"),
            @ApiResponse(responseCode = "415", description = "Unsupported media type")
    })
    @PostMapping(
            path = "/upload-image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<ImageData> uploadImage(
            @RequestPart(value = "image") @Schema(type = "string", format = "binary") MultipartFile imageFile) throws IOException {
        // Check if the provided file is of supported type (PNG)
        if (!Objects.equals(imageFile.getContentType(), "image/png")) {
            // Return a response indicating unsupported format
            return new ResponseEntity<>(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        }

        // Assuming service.uploadPhoto(imageFile) returns a valid ImageData object
        ImageData uploadedImageData = service.uploadPhoto(imageFile);

        // Return a response with the uploaded image data
        return new ResponseEntity<>(uploadedImageData, HttpStatus.OK);
    }

    @Operation(summary = "Retrieve Task State")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved task state"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping(path = "/task/{taskId}")
    public ResponseEntity<String> getTaskState(@PathVariable String taskId) {
        // Retrieve the task state using the provided taskId
        String taskState = service.getTaskState(taskId);

        // Check if the task state was found
        if (taskState != null) {
            // Return a response with the retrieved task state
            return new ResponseEntity<>(taskState, HttpStatus.OK);
        } else {
            // Return a response indicating that the task was not found
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
