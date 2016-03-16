package com.derby.nuke.wheatgrass.integration;

import org.junit.Test

import com.derby.nuke.wheatgrass.rpc.config.ConfigurationService;
import com.derby.nuke.wheatgrass.rpc.log.LogService
import com.googlecode.jsonrpc4j.JsonRpcHttpClient
import com.googlecode.jsonrpc4j.ProxyUtil

class JsonRpcTools {
	
	@Test
	void getAll(){
		JsonRpcHttpClient client = new JsonRpcHttpClient(new URL("http://127.0.0.1:8080/configuration.ci"));
		println client.invoke("getAll", null, Map.class);
		
		ConfigurationService service = ProxyUtil.createClientProxy(
			getClass().getClassLoader(),
			ConfigurationService.class,
			client);
		println service.getAll();
	}
	
	@Test
	void getLog(){
		JsonRpcHttpClient client = new JsonRpcHttpClient(new URL("http://127.0.0.1:8080/log.ci"));
		
		LogService service = ProxyUtil.createClientProxy(
			getClass().getClassLoader(),
			LogService.class,
			client);
		println service.listLogs();
	}

}
