package com.akvelon.awstest.service;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.akvelon.awstest.config.AWSSettings.*;

@Service
public class SQSService {
    public void putTaskInSQS(String taskId) {
        AmazonSQSAsync sqsAsyncClient = AmazonSQSAsyncClientBuilder.defaultClient();

        SendMessageRequest sendMessageRequest = new SendMessageRequest()
                .withQueueUrl(QUEUE_URL)
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
                .withQueueUrl(QUEUE_URL)
                .withWaitTimeSeconds(WAIT_TIME_SECONDS)
                .withMaxNumberOfMessages(MAX_NUMBER_OF_MESSAGES);

        AmazonSQSAsync sqsAsyncClient = AmazonSQSAsyncClientBuilder.defaultClient();

        for (Message message : sqsAsyncClient.receiveMessage(receiveMessageRequest).getMessages()) {
            onMessageReceivedListener.onMessageReceived(message.getBody());
            DeleteMessageRequest deleteMessageRequest = new DeleteMessageRequest(QUEUE_URL, message.getReceiptHandle());
            sqsAsyncClient.deleteMessage(deleteMessageRequest);
        }
    }
}