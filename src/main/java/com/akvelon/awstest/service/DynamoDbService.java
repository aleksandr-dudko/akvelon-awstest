package com.akvelon.awstest.service;

import com.akvelon.awstest.model.ImageData;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.GetItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;
import org.springframework.stereotype.Service;

import static com.akvelon.awstest.config.AWSSettings.*;

@Service
public class DynamoDbService {
    private final DynamoDB dynamoDB;
    private final AmazonDynamoDB client;

    public DynamoDbService() {
        // Initialize DynamoDB client
        this.client = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDB = new DynamoDB(client);
    }

    /**
     * Saves the state of a task in DynamoDB.
     *
     * @param imageData The Image object containing information about the task.
     * @param taskId    The unique identifier of the task.
     * @param state     The state to be saved for the task.
     */
    public void saveTaskState(ImageData imageData, String taskId, String state) {
        Item taskItem = new Item()
                .withString(TASK_ID, taskId)
                .withString(FILE_NAME, imageData.name())
                .withString(ORIGINAL_FILE_PATH, taskId)
                .withString(PROCESSED_FILE_PATH, taskId)
                .withString(STATE, state);

        Table dynamoTable = dynamoDB.getTable(TABLE_NAME);

        PutItemSpec putItemSpec = new PutItemSpec().withItem(taskItem);

        dynamoTable.putItem(putItemSpec);
    }

    public void updateTaskState(String id, String state) {
        Table table = dynamoDB.getTable(TABLE_NAME);

        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey(TASK_ID, id)
                .withUpdateExpression("SET #s = :val")
                .withNameMap(new NameMap().with("#s", STATE))
                .withValueMap(new ValueMap().withString(":val", state))
                .withReturnValues(ReturnValue.ALL_NEW);

        table.updateItem(updateItemSpec);
    }

    public String getTaskStateById(String taskId) {
        Table table = dynamoDB.getTable(TABLE_NAME);

        GetItemSpec getItemSpec = new GetItemSpec()
                .withPrimaryKey(TASK_ID, taskId);

        Item item = table.getItem(getItemSpec);

        if (item != null) {
            return item.getString(STATE);
        } else {
            return null;
        }
    }
}
