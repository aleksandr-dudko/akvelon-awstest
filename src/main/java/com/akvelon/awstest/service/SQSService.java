package com.akvelon.awstest.service;

import com.amazonaws.handlers.AsyncHandler;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.springframework.stereotype.Service;

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

    public void receiveMessageAsync(OnMessageReceivedListener onReceiveMessageListener) {
        AmazonSQSAsync sqsAsyncClient = AmazonSQSAsyncClientBuilder.defaultClient();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest()
                .withQueueUrl(queueUrl)
                .withWaitTimeSeconds(20)
                .withMaxNumberOfMessages(10);

        sqsAsyncClient.receiveMessageAsync(receiveMessageRequest, new AsyncHandler<>() {

            @Override
            public void onError(Exception exception) {
                exception.printStackTrace();
            }

            @Override
            public void onSuccess(ReceiveMessageRequest request, ReceiveMessageResult receiveMessageResult) {
                for (Message message : receiveMessageResult.getMessages()) {
                    System.out.println("Получено сообщение:");
                    System.out.println("Тело сообщения: " + message.getBody());
                    System.out.println("Message ID: " + message.getMessageId());
                    onReceiveMessageListener.onMessageReceived(message.getBody());
                    sqsAsyncClient.deleteMessageAsync(queueUrl, message.getReceiptHandle());
                }
            }
        });
    }

    interface OnMessageReceivedListener {
        void onMessageReceived(String messageBody);
    }
}
