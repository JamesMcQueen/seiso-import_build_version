package com.expedia.seisoimport.services;

import com.expedia.seisoimport.domain.VersionMessage;
import com.expedia.seisoimport.retrievers.SQSRetriever;
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
    private final static SQSRetriever buildVersionRetriever = new SQSRetriever();

    public void updateAPI()
    {
        boolean isDone = false;

        while(!isDone)
        {
            List<String> messages = buildVersionRetriever.retrieveSQSMessages(seisoSettings.getVersionQueueUrl(), seisoSettings.getBatchSize());
            messages.forEach(this::handleMessage);
            isDone = messages.size() <= 2;
        }
    }

	private String handleMessage(String message)
	{
		boolean changed = false;

		if(message != null)
		{
			VersionMessage versionMessage = getVersionMessage(message);
			LOGGER.info("VersionMessage: " + versionMessage.toString());

			if(seisoSettings.getEnabled() && versionMessage != null && versionMessage.isValidMessage())
			{
				final String nodeId = getNodeId(seisoSettings.getFindByNameUrl(), versionMessage.getNode());

				if(nodeId != null && nodeId.length() > 0)
				{
                    final String buildVersion = versionMessage.getBuildVersion();
					changed = updateVersion(getVersionPatch(nodeId, buildVersion));
                }
                else
				{
					LOGGER.warning(seisoSettings.getLogFailureMsg(versionMessage.toString()));
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

	private boolean updateVersion(final HttpPatch patch)
	{
		if(patch != null)
		{
			final CloseableHttpClient httpClient = HttpClients.createDefault();
			CloseableHttpResponse response;

			try
			{
				response = httpClient.execute(patch);
			}
			catch(IOException e)
			{
                LOGGER.warning(seisoSettings.getLogFailureMsg(e.getStackTrace().toString()));
				return false;
			}
			return response != null && response.getStatusLine().getStatusCode() == 200;
		}
		return false;
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
        LOGGER.info("Retrieve nodeId: " + baseUrl + nodeName);
        if(baseUrl.length() > 0 && nodeName.length() > 0)
        {
            final StringBuilder sb = new StringBuilder(baseUrl).append(nodeName);
            final HttpGet httpGet = new HttpGet(sb.toString());
            httpGet.addHeader("Accept", "*/*");
            httpGet.addHeader("Accept-Encoding", "gzip");

            try(final CloseableHttpClient client = HttpClients.createDefault())
            {
                try(CloseableHttpResponse response = client.execute(httpGet))
                {
                    final HttpEntity entity = response.getEntity();

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
                LOGGER.warning(seisoSettings.getLogFailureMsg(e.toString()));
            }
        }
        return null;
    }

	private HttpPatch getVersionPatch(String patchAPI, String version)
	{
        final HttpPatch httpPatch = new HttpPatch(patchAPI);
		httpPatch.addHeader("Accept", "*/*");
		httpPatch.addHeader("Content-Type", "application/json");

		httpPatch.addHeader(BasicScheme.authenticate(new UsernamePasswordCredentials(
                seisoSettings.getApiUser(), seisoSettings.getApiPassword()), "UTF-8", false));

		final JsonObject patchData = new JsonObject();
        patchData.addProperty("buildVersion", version);
		final StringEntity entity = new StringEntity(patchData.toString(), "UTF-8");
		entity.setContentType("application/json");
		httpPatch.setEntity(entity);

        LOGGER.info("httpPatch: " + httpPatch.toString());

		return httpPatch;
	}
}
