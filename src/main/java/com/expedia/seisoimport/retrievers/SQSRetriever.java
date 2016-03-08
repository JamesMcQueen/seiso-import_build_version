package com.expedia.seisoimport.retrievers;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 3/6/16
 */
public class SQSRetriever
{
    private final static Logger LOGGER = Logger.getLogger(SQSRetriever.class.getName());
    private static AmazonSQS SQS = new AmazonSQSClient();


    public List<String> retrieveSQSMessages(String sqsUrl, Integer chunkSize)
    {
        SQS.setRegion(Regions.getCurrentRegion());
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsUrl);
        receiveMessageRequest.setMaxNumberOfMessages(chunkSize);
        List<String> messageList = new ArrayList(chunkSize);

        for(Message m: SQS.receiveMessage(receiveMessageRequest).getMessages())
        {
            messageList.add(m.getBody());
            removeMessage(m, sqsUrl);
        }

        LOGGER.info("Message Count: " + messageList.size());

        return messageList;
    }

    private void removeMessage(final Message message, String queueUrl)
    {
        LOGGER.info("Deleting a message.");
        String messageReceiptHandle = message.getReceiptHandle();
        SQS.deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(messageReceiptHandle));
    }
}
