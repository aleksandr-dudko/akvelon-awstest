package com.akvelon.awstest.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Service
public class SQSService {
    // Specify your SQS queue URL
    private final String queueUrl = "AWSTest-queue";

    public void putTaskInSQS(String taskId) {
        AmazonSQSAsync sqsAsyncClient = AmazonSQSAsyncClientBuilder.defaultClient();

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(taskId);

        Future<SendMessageResult> future = sqsAsyncClient.sendMessageAsync(sendMessageRequest);

        try {
            SendMessageResult sendMessageResult = future.get();
            System.out.println("Task " + taskId + " added to SQS asynchronously. Message ID: " + sendMessageResult.getMessageId());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        } finally {
            sqsAsyncClient.shutdown();
        }
    }

    interface OnMessageReceivedListener {
        void onMessageReceived(String messageBody) throws IOException;
    }

    public void receiveMessages(OnMessageReceivedListener onMessageReceivedListener) throws IOException {
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withWaitTimeSeconds(20)
                .withMaxNumberOfMessages(10);

        AmazonSQSAsync sqsAsyncClient = AmazonSQSAsyncClientBuilder.defaultClient();

        for (Message message : sqsAsyncClient.receiveMessage(receiveMessageRequest).getMessages()) {
            onMessageReceivedListener.onMessageReceived(message.getBody());
            DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(queueUrl, message.getReceiptHandle());
            sqsAsyncClient.deleteMessage(deleteMessageRequest);
        }
    }
}
