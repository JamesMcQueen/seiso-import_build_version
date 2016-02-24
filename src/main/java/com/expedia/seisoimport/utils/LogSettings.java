package com.expedia.seisoimport.utils;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.logging.LogLevel;
import org.springframework.stereotype.Component;

import java.util.logging.Level;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 2/10/16
 */
@Component
@ConfigurationProperties(prefix = "logging")
public class LogSettings
{
	private Level appLogLevel = Level.INFO;
	private LogLevel springLogLevel = LogLevel.INFO;
	private String logFile;

	public LogSettings()
	{
		// No args constructor
	}

	public String getLogFileLocation()
	{
		return logFile;
	}

	public void setLogFile(String logFile)
	{
		this.logFile = logFile;
	}

	public Level getAppLogLevel()
	{
		return appLogLevel != null ? appLogLevel : Level.INFO;
	}

	public void setAppLogLevel(String logLevel)
	{
		if(logLevel != null && logLevel.length() > 0)
		{
			Level converted = Level.parse(logLevel);

			if(converted != null)
			{
				this.appLogLevel = converted;
			}
		}
	}

	public LogLevel getSpringLogLevel()
	{
		return springLogLevel != null ? springLogLevel : LogLevel.INFO;
	}

	public void setSpringLogLevel(String logLevel)
	{
		if(logLevel != null && logLevel.length() > 0)
		{
			LogLevel converted = LogLevel.valueOf(logLevel);

			if(converted != null)
			{
				this.springLogLevel = converted;
			}
		}
	}
}
