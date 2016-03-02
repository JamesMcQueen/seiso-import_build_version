package com.expedia.seisoimport.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/28/16
 */
@Component
@ConfigurationProperties(prefix = "seisoSettings")
public class SeisoSettings
{
	private String findByNameUrl;
	private String apiUser;
	private String apiPassword;
	private String logSuccessMsg;
	private String logFailureMsg;
	private Boolean isActive;

	public SeisoSettings()
	{
		// no args constructor
	}

	public String getFindByNameUrl()
	{
		return findByNameUrl;
	}

	public void setFindByNameUrl(String findByNameUrl)
	{
		this.findByNameUrl = findByNameUrl;
	}

	public String getApiUser()
	{
		return apiUser;
	}

	public void setApiUser(String apiUser)
	{
		this.apiUser = apiUser;
	}

	public String getApiPassword()
	{
		return apiPassword;
	}

	public void setApiPassword(String apiPassword)
	{
		this.apiPassword = apiPassword;
	}

	public String getLogSuccessMessage()
	{
		return logSuccessMsg;
	}

	public void setLogSuccessMessage(String logSuccessMsg)
	{
		this.logSuccessMsg = logSuccessMsg;
	}

	public String getLogFailureMessage(String exception)
	{
		if(exception == null || exception.length() < 1)
		{
			return logFailureMsg;
		}
		return logFailureMsg + exception;
	}

	public void setLogFailureMessage(String logFailureMsg)
	{
		this.logFailureMsg = logFailureMsg;
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
