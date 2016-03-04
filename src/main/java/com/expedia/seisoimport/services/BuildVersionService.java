package com.expedia.seisoimport.services;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.expedia.seisoimport.domain.VersionMessage;
import com.expedia.seisoimport.utils.SeisoSettings;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/22/16
 */
@Service("buildVersionService")
public class BuildVersionService implements UpdateService
{
	@Autowired
	private SeisoSettings seisoSettings;

	private final static Logger LOGGER = Logger.getLogger(BuildVersionService.class.getName());
	private final static GsonJsonParser JSON_PARSER = new GsonJsonParser();
    private static final String SQS_QUEUE_URL = "https://sqs.us-west-2.amazonaws.com/408096535527/minion-NodeNotificationChannelType-VersionChanged";

    private static AWSCredentials CREDENTIALS = null;
    private static AmazonSQS SQS = null;


    public void updateAPI()
    {
        // Get messages from queue as strings
        List<String> messages = retrieveSQSMessages();

        // Update using handleMessage for each message
        for(String message : messages)
        {
            handleMessage(message);
        }
    }

    private void initialize()
    {
        try
        {
            CREDENTIALS = new InstanceProfileCredentialsProvider().getCredentials();
            SQS = new AmazonSQSClient(CREDENTIALS);
            Region usWest2 = Region.getRegion(Regions.US_WEST_2);
            SQS.setRegion(usWest2);
        }
        catch (Exception e)
        {
            throw new AmazonClientException(
                    "Cannot load the CREDENTIALS from the credential profiles file. " +
                            "Please make sure that your CREDENTIALS file is at the correct " +
                            "location (~/.aws/CREDENTIALS), and is in valid format.",
                    e);
        }
    }

	private String handleMessage(String message)
	{
		boolean changed = false;

		if(message != null)
		{
			VersionMessage versionMessage = getVersionMessage(message);

			LOGGER.info("VersionMessageObject: " + versionMessage.toString());
            LOGGER.info("getEnabled: " + seisoSettings.getEnabled());

			if(versionMessage != null && versionMessage.isValidMessage())
			{
				final String nodeId = getNodeId(seisoSettings.getFindByNameUrl(), versionMessage.getNode());

				if(nodeId != null && nodeId.length() > 0)
				{
                    LOGGER.info("Node is valid getting the build version");
                    final String buildVersion = versionMessage.getBuildVersion();
					changed = updateVersion(getVersionPatch(nodeId, buildVersion));
                }
                else
				{
					LOGGER.info(seisoSettings.getLogFailureMsg(versionMessage.toString()));
				}
			}
		}

		if(changed)
		{
			LOGGER.info(seisoSettings.getLogSuccessMsg());
		}
		return "Version Updated?: " + changed;
	}

	private VersionMessage getVersionMessage(String message)
	{
		Map searchMap = JSON_PARSER.parseMap(JSON_PARSER.parseMap(message).get("Message").toString());

		if(message != null && searchMap != null)
		{
			searchMap = JSON_PARSER.parseMap(searchMap.get("Fields").toString());
			return new GsonBuilder().create().fromJson(searchMap.toString(), VersionMessage.class);
		}

		return null;
	}

	/**
	 * Retrieves a Seiso Node's Id given an endpoint and the name of a node.
	 *
	 * @param baseUrl  the endpoint
	 * @param nodeName the name of the node
	 * @return a valid id or null on failure.
	 */
	private String getNodeId(String baseUrl, String nodeName)
	{
        LOGGER.info("Getting Node Id: " + baseUrl + ", " + nodeName);
		if(baseUrl.length() > 0 && nodeName.length() > 0)
		{
			final StringBuilder sb = new StringBuilder(baseUrl).append(nodeName);
			final HttpGet httpGet = new HttpGet(sb.toString());
			httpGet.addHeader("Accept", "*/*");
			httpGet.addHeader("Accept-Encoding", "gzip");

            LOGGER.info("Attempting to create a closeable client");
			try(final CloseableHttpClient client = HttpClients.createDefault())
			{
                LOGGER.info("Attempting to generate response");
                try(CloseableHttpResponse response = client.execute(httpGet))
				{
					final HttpEntity entity = response.getEntity();

                    LOGGER.info("Entity: " + response.getEntity());

					if(entity != null)
					{
						final JsonReader reader = new JsonReader(new InputStreamReader(entity.getContent()));

						return new JsonParser().parse(reader).getAsJsonObject()
								.get("_links").getAsJsonObject()
								.get("self").getAsJsonObject()
								.get("href").getAsString();
					}
				}
			}
			catch(IOException e)
			{
				System.out.println(e);
                LOGGER.info(seisoSettings.getLogFailureMsg(e.toString()));
			}
		}
		return null;
	}

	private boolean updateVersion(final HttpPatch patch)
	{
        LOGGER.info("UPDATE VERSION");
		if(patch != null)
		{
			final CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response = null;

			try
			{
				response = httpClient.execute(patch);
			}
			catch(IOException e)
			{
				return false;
			}
			return response != null && response.getStatusLine().getStatusCode() == 200;
		}
		return false;
	}

	private HttpPatch getVersionPatch(String patchAPI, String version)
	{
        LOGGER.info("Patching with buildVersion: " + version);

        final HttpPatch httpPatch = new HttpPatch(patchAPI);
		httpPatch.addHeader("Accept", "*/*");
		httpPatch.addHeader("Content-Type", "application/json");

		// We should be able to eliminate this when 401 issue is resolved.
		httpPatch.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(seisoSettings.getApiUser(), seisoSettings.getApiPassword()), "UTF-8", false));

		final JsonObject patchData = new JsonObject();
        patchData.addProperty("buildVersion", version);
		final StringEntity entity = new StringEntity(patchData.toString(), "UTF-8");
		entity.setContentType("application/json");
		httpPatch.setEntity(entity);

        LOGGER.info("httpPatch: " + httpPatch.toString());

		return httpPatch;
	}

    public List<String> retrieveSQSMessages()
    {
        // String queueUrl = seisoSettings.getQueueUrl;

        if(CREDENTIALS == null)
        {
            initialize();
        }

        LOGGER.info("Receiving messages from: " + SQS_QUEUE_URL);
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(SQS_QUEUE_URL);

        List<String> messageList = new ArrayList<>();

        for(Message m: SQS.receiveMessage(receiveMessageRequest).getMessages())
        {
            messageList.add(m.getBody());
            removeMessage(m, SQS_QUEUE_URL);
        }

        LOGGER.info("message list size: " + messageList.size());

        return messageList;
    }

    public void removeMessage(final Message message, String queueUrl)
    {
        // Delete a message
        LOGGER.info("Deleting a message.");
        String messageReceiptHandle = message.getReceiptHandle();
        SQS.deleteMessage(new DeleteMessageRequest().withQueueUrl(queueUrl).withReceiptHandle(messageReceiptHandle));

    }
}
