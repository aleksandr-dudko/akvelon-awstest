package com.akvelon.awstest.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DynamoDbService {
    private final DynamoDB dynamoDB;
    private final String tableName;
    private AmazonDynamoDB client;

    public DynamoDbService() {
        // Initialize DynamoDB client
        this.client = AmazonDynamoDBClientBuilder.standard().build();
        this.dynamoDB = new DynamoDB(client);
        // Specify your DynamoDB table name
        this.tableName = "AWStest-dynamo-table";
    }

    public void saveTaskState(String fileName, String originalFilePath, String processedFilePath, String taskId, String state) {
        try {
            Item item = new Item()
                    .withString("FileName", fileName)
                    .withString("OriginalFilePath", originalFilePath)
                    .withString("ProcessedFilePath", processedFilePath)
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

    public void updateTaskState(String id, String processedFilePath, String state) {
        try {
            // Формируем запрос на обновление
            UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                    .withTableName(tableName)
                    .withKey(Map.of("TaskId", new AttributeValue().withS(id)))
                    .withExpressionAttributeValues(Map.of(
                            ":ProcessedFilePath", new AttributeValue().withS(processedFilePath),
                            ":State", new AttributeValue().withS(state)
                    ));

            // Выполняем обновление
            client.updateItem(updateItemRequest);

        } catch (AmazonServiceException e) {
            System.err.println("AmazonServiceException: " + e.getErrorMessage());
            System.err.println("Status Code: " + e.getStatusCode());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Request ID: " + e.getRequestId());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error saving task state: " + e.getMessage());
        }
    }
}
