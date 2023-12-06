package com.akvelon.awstest.service;

import com.akvelon.awstest.model.Image;
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

@Service
public class DynamoDbService {
    private final DynamoDB dynamoDB;
    private final String tableName;
    private final AmazonDynamoDB client;

    public DynamoDbService() {
        // Initialize DynamoDB client
        this.client = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDB = new DynamoDB(client);
        // Specify your DynamoDB table name
        this.tableName = "AWStest-dynamo-table";
    }

    public void saveTaskState(Image image, String taskId, String state) {
        try {
            Item item = new Item()
                    .withString("FileName", image.name())
                    .withString("OriginalFilePath", image.getOriginalPath())
                    .withString("ProcessedFilePath", image.getProcessedPath())
                    .withString("TaskId", taskId)
                    .withString("State", state);

            Table table = dynamoDB.getTable(tableName);

            PutItemSpec putItemSpec = new PutItemSpec().withItem(item);

            PutItemOutcome outcome = table.putItem(putItemSpec);

            System.out.println("Task state saved successfully. PutItemOutcome: " + outcome);
        } catch (Exception e) {
            System.err.println("Error saving task state: " + e.getMessage());
        }
    }

    public void updateTaskState(String id, String state) {
        try {
            Table table = dynamoDB.getTable(tableName);

            UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                    .withPrimaryKey("TaskId", id)
                    .withUpdateExpression("SET #s = :val")
                    .withNameMap(new NameMap().with("#s", "State"))
                    .withValueMap(new ValueMap().withString(":val", state))
                    .withReturnValues(ReturnValue.ALL_NEW);

            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            System.out.println("Task state updated successfully. UpdateItemOutcome: " + outcome);
        } catch (Exception e) {
            System.err.println("Error updating task state: " + e.getMessage());
        }
    }

    public String getTaskStateById(String taskId) {
        Table table = dynamoDB.getTable(tableName);

        GetItemSpec getItemSpec = new GetItemSpec()
                .withPrimaryKey("TaskId", taskId);

        Item item = table.getItem(getItemSpec);

        if (item != null) {
            return item.getString("State");
        } else {
            return null;
        }
    }
}
