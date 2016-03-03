package com.expedia.seisoimport.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 3/2/16
 */
public class SeisoSettingsTest
{
    @Autowired
    SeisoSettings seisoSettings;

    @Test
    public void getFindByNameUrl()
    {
        assertEquals(seisoSettings.getFindByNameUrl(), "https://seiso-api.example.expedia.com/api/nodes/search/findByName?name=");
    }

    @Test
    public void getApiUserTest()
    {
        assertEquals(seisoSettings.getApiUser(), "default");
    }

    @Test
    public void getApiPasswordTest()
    {
        assertEquals(seisoSettings.getApiPassword(), "default");
    }

    @Test
    public void getIsActive()
    {
        assertEquals(seisoSettings.getEnabled(), true);
    }

}
