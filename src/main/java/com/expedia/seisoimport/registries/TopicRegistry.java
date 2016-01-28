package com.expedia.seisoimport.registries;

import com.amazonaws.ResponseMetadata;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.DeleteTopicRequest;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.amazonaws.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/25/16
 */
@Component("topicRepo")
public class TopicRegistry
{
	private static List<String> topics = null;

	public TopicRegistry()
	{

	}

	public boolean addTopic(String topicArn, String protocol, Regions region)
	{
		SubscribeRequest subRequest = new SubscribeRequest(topicArn, protocol, "https://localhost:8080");
		SubscribeResult result = null;

		if(subRequest != null)
		{
			AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());
			snsClient.setRegion(Region.getRegion(region));
			result = snsClient.subscribe(subRequest);
		}

		if(result != null)
		{
			String subArn = result.getSubscriptionArn();
			return !StringUtils.isNullOrEmpty(subArn) && subArn.matches(topicArn);
		}
		return false;
	}

	public boolean removeTopic(String topicArn)
	{
		AmazonSNSClient snsClient = new AmazonSNSClient(new ClasspathPropertiesFileCredentialsProvider());

		if(!StringUtils.isNullOrEmpty(topicArn))
		{
			DeleteTopicRequest deleteRequest = new DeleteTopicRequest(topicArn);
			snsClient.deleteTopic(deleteRequest);
			ResponseMetadata deleteResponseData = snsClient.getCachedResponseMetadata(deleteRequest);

			return deleteResponseData != null && !StringUtils.isNullOrEmpty(deleteResponseData.toString());
		}
		return false;
	}

	public static List<String> getTopicArns()
	{
		ensureTopics();
		return topics;
	}

	private static void ensureTopics()
	{
		if(topics == null)
		{
			buildTopics();
		}
	}

	// TODO: Create topics from a db or config file.
	private static void buildTopics()
	{
		List<String> values = new ArrayList<>();
		values.add("topicArn1");

		for(String configValue : values)
		{
			topics.add(configValue);
		}
	}
}
