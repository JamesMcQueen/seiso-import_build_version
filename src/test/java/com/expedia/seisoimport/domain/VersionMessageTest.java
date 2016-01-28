package com.expedia.seisoimport.domain;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 12/15/15
 */

public class VersionMessageTest
{
	@Test
	public void nullValueTest()
	{
		final VersionMessage message = new VersionMessage(null, null, null);
		final String not_set = "not set";
		assertEquals(not_set, message.getBuildVersion());
		assertEquals(not_set, message.getNode());
		assertEquals(not_set, message.getServiceInstance());
	}

	@Test
	public void getterTest()
	{
		final String node = "NODE";
		final String si = "SI";
		final String bv = "BV";
		final VersionMessage message = new VersionMessage(si, node, bv);
		assertEquals(node, message.getNode());
		assertEquals(si, message.getServiceInstance());
		assertEquals(bv, message.getBuildVersion());

	}
}
