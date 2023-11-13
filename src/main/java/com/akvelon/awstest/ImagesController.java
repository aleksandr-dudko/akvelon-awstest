package com.akvelon.awstest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/images")
public class ImagesController {
    @Operation(summary = "Saves file in S3")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Image saved")})
    @RequestMapping(
            path = "/upload",
            method = RequestMethod.POST,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadImage(@RequestPart(value = "image")  @Schema(type = "string", format = "binary") MultipartFile file,
                                              @RequestParam("someId") Long someId) {
/*
        CompletableFuture<JsonNode> completableFuture = orderService.getOrders(key);
        JsonNode jsonNode = completableFuture.get();*/

        return ResponseEntity.ok(null);
    }
}
