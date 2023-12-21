package com.akvelon.awstest.config;

import org.springframework.http.MediaType;

public class Constants {
    public static final String FILE_NAME = "FileName";
    public static final String ORIGINAL_FILE_PATH = "OriginalFilePath";
    public static final String PROCESSED_FILE_PATH = "ProcessedFilePath";
    public static final String TASK_ID = "TaskId";
    public static final String STATE = "State";

    public static final String ORIGINAL = "original";
    public static final int ROTATION_ANGLE = 180;
    public static final String FILE_PREFIX = "rotated_";
    public static final String FILE_SUFFIX = ".png";
    public static final String PARAMETER_NAME = "file";
    public static final String MEDIA_TYPE = MediaType.IMAGE_PNG_VALUE;
    public static final String PROCESSED_FOLDER = "processed";


    public static final String CONTENT_TYPE_IMAGE_PNG = "image/png";
    public static final String USER_METADATA_ID = "id";
    public static final String USER_METADATA_NAME = "name";
    public static final String USER_METADATA_SIZE = "size";

    public static final int WAIT_TIME_SECONDS = 20;
    public static final int MAX_NUMBER_OF_MESSAGES = 10;
}
