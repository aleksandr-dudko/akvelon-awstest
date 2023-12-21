package com.akvelon.awstest.model;

public record ImageData(Long id, String bucket, String name) {
    public String getOriginalPath() {
        return "s3://" + bucket + "/Original/" + name;
    }

    public String getProcessedPath() {
        return "s3://" + bucket + "/Processed/" + name;
    }
}
