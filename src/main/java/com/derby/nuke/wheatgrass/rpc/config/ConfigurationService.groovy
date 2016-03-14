package com.derby.nuke.wheatgrass.rpc.config;

import com.googlecode.jsonrpc4j.JsonRpcMethod
import com.googlecode.jsonrpc4j.JsonRpcService

@JsonRpcService("/configuration.ci")
interface ConfigurationService {
	
	@JsonRpcMethod("get")
	String getProperty(String key);

	@JsonRpcMethod("getAll")
	Map<String,String> getAll();
	
	@JsonRpcMethod("update")
	void setProperty(String key, String value);
	
}
