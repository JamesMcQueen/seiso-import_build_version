package com.expedia.seisoimport.domain;

import com.google.gson.annotations.SerializedName;

/**
 * Author: James McQueen (jmcqueen@expedia.com)
 * Created: 1/22/16
 */
public class VersionMessage
{
	public static final String NOT_SET = "not set";

	@SerializedName("Version")
	private final String buildVersion;
	@SerializedName("Node")
	private final String node;
	@SerializedName("ServiceInstance")
	private final String serviceInstance;

	public VersionMessage(String serviceInstance, String node, String buildVersion)
	{
		this.serviceInstance = serviceInstance;
		this.node = node;
		this.buildVersion = buildVersion;
	}

	public String getBuildVersion()
	{
		if(buildVersion != null)
		{
			return buildVersion;
		}
		return NOT_SET;
	}

	public String getServiceInstance()
	{
		if(serviceInstance != null)
		{
			return serviceInstance;
		}
		return NOT_SET;
	}

	public String getNode()
	{
		if(node != null)
		{
			return node;
		}
		return NOT_SET;
	}

	public boolean isValidMessage()
	{
		if(buildVersion != null && serviceInstance != null && node != null
				&& !buildVersion.equals(NOT_SET) && !serviceInstance.equals(NOT_SET) && !node.equals(NOT_SET))
		{
			return true;
		}
		return false;
	}

	@Override
	public String toString()
	{
		return String.format("VersionMessage:[ServiceInstance=%s,Node=%s,BuildVersion=%s]", serviceInstance, node, buildVersion);
	}
}
