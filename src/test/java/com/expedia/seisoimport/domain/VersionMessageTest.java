package com.expedia.seisoimport.domain;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

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
        assertEquals(false, message.isValidMessage());
	}

	@Test
	public void getterTest()
	{
		VersionMessage message = createEmptyMessage();
		assertEquals("NODE", message.getNode());
		assertEquals("SI", message.getServiceInstance());
		assertEquals("BV", message.getBuildVersion());
	}

    @Test
    public void isValidOnEmptyTest()
    {
        VersionMessage message = createEmptyMessage();
        assertFalse(message.isValidMessage());
    }

    private VersionMessage createEmptyMessage()
    {
        final String node = "NODE";
        final String si = "SI";
        final String bv = "BV";

        return new VersionMessage(si, node, bv);
    }
}
