package com.akvelon.awstest.config;

import com.amazonaws.regions.Regions;
import org.springframework.http.MediaType;

public class AWSSettings {
    public static final String TABLE_NAME = "AWStest-dynamo-table";
    public static final String FILE_NAME = "FileName";
    public static final String ORIGINAL_FILE_PATH = "OriginalFilePath";
    public static final String PROCESSED_FILE_PATH = "ProcessedFilePath";
    public static final String TASK_ID = "TaskId";
    public static final String STATE = "State";

    public static final String ORIGINAL = "original";
    public static final String CREATED_STATE = "Created";
    public static final int ROTATION_ANGLE = 180;
    public static final String FILE_PREFIX = "rotated_";
    public static final String FILE_SUFFIX = ".png";
    public static final String PARAMETER_NAME = "file";
    public static final String MEDIA_TYPE = MediaType.IMAGE_PNG_VALUE;
    public static final String IN_PROGRESS_STATE = "InProgress";
    public static final String PROCESSED_FOLDER = "processed";
    public static final String DONE_STATE = "Done";

    public static final Regions CLIENT_REGION = Regions.US_EAST_1;
    public static final String BUCKET_NAME = "dudko-aws-test-bucket";
    public static final String CONTENT_TYPE_IMAGE_PNG = "image/png";
    public static final String USER_METADATA_ID = "id";
    public static final String USER_METADATA_NAME = "name";
    public static final String USER_METADATA_SIZE = "size";

    public static final String QUEUE_URL = "AWSTest-queue";
    public static final int WAIT_TIME_SECONDS = 20;
    public static final int MAX_NUMBER_OF_MESSAGES = 10;
}
