package com.jackson.aws.sqs;

/**
 * Created by jackson on 2017/7/15.
 */

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;

import java.util.Date;
import java.util.List;

public class SendReceiveMessages
{
    private static final String QUEUE_NAME = "testQueue" + new Date().getTime();
    private static final String accessKey = "AKIAJK5HYPF27XUV6KFQ";
    private static final String accessSecret = "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";
    public static void main(String[] args)
    {
        // 初始化客户端
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey,accessSecret);
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.US_EAST_2)
                .build();

//        try {
//            CreateQueueResult create_result = sqs.createQueue(QUEUE_NAME);
//            System.out.println(create_result);
//        } catch (AmazonSQSException e) {
//            if (!e.getErrorCode().equals("QueueAlreadyExists")) {
//                throw e;
//            }
//        }

        String queueUrl = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();

        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody("hello world2")
                .withDelaySeconds(5);
        sqs.sendMessage(send_msg_request);


        // Send multiple messages to the queue
        SendMessageBatchRequest send_batch_request = new SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(
                        new SendMessageBatchRequestEntry(
                                "msg", "Hello 22from message 5"),
                        new SendMessageBatchRequestEntry(
                                "msg_4i", "Hello 22from message 6")
                                .withDelaySeconds(10));
        sqs.sendMessageBatch(send_batch_request);

        // receive messages from the queue
        List<Message> messages = sqs.receiveMessage(queueUrl).getMessages();

        // delete messages from the queue
//        for (Message m : messages) {
//            sqs.deleteMessage(queueUrl, m.getReceiptHandle());
//        }
    }
}
