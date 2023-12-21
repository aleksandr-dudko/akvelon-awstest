package com.akvelon.awstest.config;

import com.amazonaws.regions.Regions;

public class AWSSettings {
    public static final Regions CLIENT_REGION = Regions.US_EAST_1;
    public static final String TABLE_NAME = "AWStest-dynamo-table";
    public static final String BUCKET_NAME = "dudko-aws-test-bucket";
    public static final String QUEUE_URL = "AWSTest-queue";
}
