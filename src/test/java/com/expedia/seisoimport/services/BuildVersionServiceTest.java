package com.expedia.seisoimport.services;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import org.easymock.EasyMock;
import org.junit.Test;
import org.testng.annotations.BeforeTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/22/16
 */
public class BuildVersionServiceTest
{
	BuildVersionService importer = new BuildVersionService();
	SNSEvent event = new SNSEvent();

	@BeforeTest
	public void setUp()
	{
		importer = new BuildVersionService();
		event = createStubbedEvent();
	}

	@Test
	public void testNullSNSEvent()
	{
		//assertEquals(importer.handleRequest(null, getMockContext()), false);
	}

	private Context getMockContext()
	{
		final Logger logger = EasyMock.createNiceMock(Logger.class);
		EasyMock.replay(logger);

		final Context context = EasyMock.createNiceMock(Context.class);
		//EasyMock.expect(context.getLogger()).andReturn(logger).anyTimes();
		EasyMock.expect(context.getLogStreamName()).andReturn("").anyTimes();
		EasyMock.replay(context);

		return context;
	}

	private SNSEvent createStubbedEvent()
	{
		final SNSEvent event = new SNSEvent();
		event.setRecords(getMockSNSRecords());
		return event;
	}

	private List<SNSEvent.SNSRecord> getMockSNSRecords()
	{
		final SNSEvent.SNS sns = EasyMock.createNiceMock(SNSEvent.SNS.class);
		EasyMock.expect(sns.getMessage()).andReturn("Node version has changed").anyTimes();
		EasyMock.expect(sns.getType()).andReturn("Notification").anyTimes();
		EasyMock.expect(sns.getMessageId()).andReturn("7c004319-8514-5ce5-b56d-0dcf65f744d3").anyTimes();
		EasyMock.expect(sns.getTopicArn()).andReturn("arn:aws:sns:us-west-2:408096535527:minion-NodeNotificationChannelType-VersionChanged").anyTimes();
		EasyMock.expect(sns.getSubject()).andReturn("expweb-trunk-latest-b").anyTimes();
		final Map mockAttributes = getMockAttributeMap();
		EasyMock.expect(sns.getMessageAttributes()).andReturn(mockAttributes).anyTimes();
		EasyMock.replay(sns);

		final SNSEvent.SNSRecord record = new SNSEvent.SNSRecord();
		record.setSns(sns);

		final List<SNSEvent.SNSRecord> records = new ArrayList();
		records.add(record);

		return records;
	}

	private Map<String, SNSEvent.MessageAttribute> getMockAttributeMap()
	{
		final Map<String, SNSEvent.MessageAttribute> fieldMap = EasyMock.createNiceMock(Map.class);

		final Map<String, SNSEvent.MessageAttribute> fields = new HashMap<String, SNSEvent.MessageAttribute>();
		fields.put("ServiceInstance", getMockAttribute("expweb-trunk-latest-b"));
		fields.put("Node", getMockAttribute("expweb-trunk-latest-b"));
		fields.put("Version", getMockAttribute("12345"));
		fields.put("EventGuid", getMockAttribute("a10f99d8-c4be-49a2-b9f5-5f679a51c27d"));
		fields.put("Topic", getMockAttribute("minion-NodeNotificationChannelType-VersionChanged"));
		fields.put("Subject", getMockAttribute("expweb-trunk-latest-b"));


		EasyMock.expect(fieldMap.get("ServiceInstance")).andReturn(getMockAttribute("expweb-trunk-latest-b")).anyTimes();
		EasyMock.expect(fieldMap.get("Node")).andReturn(getMockAttribute("chexwbnnginx001-webrouter")).anyTimes();
		EasyMock.expect(fieldMap.get("Version")).andReturn(getMockAttribute("12345")).anyTimes();
		EasyMock.expect(fieldMap.get("EventGuid")).andReturn(getMockAttribute("a10f99d8-c4be-49a2-b9f5-5f679a51c27d")).anyTimes();
		EasyMock.expect(fieldMap.get("Topic")).andReturn(getMockAttribute("minion-NodeNotificationChannelType-VersionChanged")).anyTimes();
		EasyMock.expect(fieldMap.get("Subject")).andReturn(getMockAttribute("expweb-trunk-latest-b")).anyTimes();
		EasyMock.replay(fieldMap);

		return fields;
	}

	private SNSEvent.MessageAttribute getMockAttribute(String value)
	{
		final SNSEvent.MessageAttribute attribute = EasyMock.createNiceMock(SNSEvent.MessageAttribute.class);
		EasyMock.expect(attribute.getValue()).andReturn(value);
		EasyMock.replay(attribute);
		return attribute;
	}
}
