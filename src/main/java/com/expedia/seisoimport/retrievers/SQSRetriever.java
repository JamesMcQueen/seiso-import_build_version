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
    private static AWSCredentials CREDENTIALS = null;
    private static AmazonSQS SQS = null;


    public List<String> retrieveSQSMessages(String sqsUrl, Integer chunkSize)
    {
        if(CREDENTIALS == null)
        {
            initialize();
        }

        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(sqsUrl);
        receiveMessageRequest.setMaxNumberOfMessages(20);
        List<String> messageList = new ArrayList(20);

        for(Message m: SQS.receiveMessage(receiveMessageRequest).getMessages())
        {
            messageList.add(m.getBody());
            removeMessage(m, sqsUrl);
        }

        LOGGER.info("Message Count: " + messageList.size());

        return messageList;
    }

    private void initialize()
    {
        try
        {
            CREDENTIALS = new InstanceProfileCredentialsProvider().getCredentials();
            SQS = new AmazonSQSClient(CREDENTIALS);
            LOGGER.info(Regions.getCurrentRegion().toString());
            Region usWest2 = Region.getRegion(Regions.US_WEST_2);
            SQS.setRegion(usWest2);
        }
        catch (Exception e)
        {
            throw new AmazonClientException(
                    "Cannot load the CREDENTIALS from the credential profiles file. " +
                            "Please make sure that your CREDENTIALS file is at the correct " +
                            "location (~/.aws/CREDENTIALS), and is in valid format.", e);
        }
    }

    private void removeMessage(final Message message, String queueUrl)
    {
        LOGGER.info("Deleting a message.");
        String messageReceiptHandle = message.getReceiptHandle();
        SQS.deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(messageReceiptHandle));
    }
}
