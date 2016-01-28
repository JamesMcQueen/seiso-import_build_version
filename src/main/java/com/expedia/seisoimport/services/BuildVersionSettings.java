package com.expedia.seisoimport.services;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/28/16
 */
@Component
@ConfigurationProperties(prefix = "buildVersionSettings")
public class BuildVersionSettings
{
	private String logSuccess;
	private String logFailure;
	private String findByNameURL;
	private String fields;
	private boolean isActive = false;

	public BuildVersionSettings()
	{
		// no args constructor
	}

	public String getLogSuccess()
	{
		return logSuccess;
	}

	public void setLogSuccess(String logSuccess)
	{
		this.logSuccess = logSuccess;
	}

	public String getLogFailure()
	{
		return logFailure;
	}

	public void setLogFailure(String logFailure)
	{
		this.logFailure = logFailure;
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
}
