package com.expedia.seisoimport.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/28/16
 */
@Component
@ConfigurationProperties(prefix = "buildVersionSettings")
public class BuildVersionSettings
{
	private String findByNameURL;
	private String fields;
	private boolean isActive = false;
	private String username;
	private String password;

	public BuildVersionSettings()
	{
		// no args constructor
	}

	public String getFindByNameURL()
	{
		return findByNameURL;
	}

	public void setFindByNameURL(String findByNameURL)
	{
		this.findByNameURL = findByNameURL;
	}

	public String getFieldKey()
	{
		return fields;
	}

	public void setFieldKey(String fields)
	{
		this.fields = fields;
	}

	public boolean isActive()
	{
		return isActive;
	}

	public void setActive(boolean isActive)
	{
		this.isActive = isActive;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}
}
