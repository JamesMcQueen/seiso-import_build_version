package com.expedia.seisoimport.controllers;

import com.expedia.seisoimport.services.BuildVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/22/16
 */

@RestController
public class MessageReciever
{
	@Autowired
	BuildVersionService buildVersionService;

	// TODO: Figure out best way to show README.md
	@RequestMapping(value = "/readme", method = RequestMethod.GET)
	public String readme()
	{
		return "README.md is not set";
	}

	@RequestMapping(value = "/updateVersion", method = RequestMethod.POST)
	public void updateVersion(@RequestBody String message)
	{
		buildVersionService.handleRequest(message);
	}

}
