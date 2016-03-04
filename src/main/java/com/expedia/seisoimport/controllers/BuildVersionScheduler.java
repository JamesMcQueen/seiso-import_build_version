package com.expedia.seisoimport.controllers;

import com.expedia.seisoimport.services.BuildVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 3/3/16
 */
@Component
public class BuildVersionScheduler
{
    @Autowired
    BuildVersionService buildVersionService;

    @Scheduled(cron = "0 * * * * MON-FRI")
    public void update()
    {
        buildVersionService.updateAPI();
    }

}
