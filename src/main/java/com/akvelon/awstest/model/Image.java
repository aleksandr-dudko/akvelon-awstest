package com.akvelon.awstest.model;

public record Image(Long id, String bucket, String name, String folder) {
    public String getOriginalPath() {
        return "s3://" + bucket + "/" + folder + "/" + name;
    }

    public String getProcessedPath() {
        return "s3://" + bucket + "/" + folder + "/" + name;
    }
}
