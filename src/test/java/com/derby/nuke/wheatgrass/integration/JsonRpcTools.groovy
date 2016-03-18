package com.derby.nuke.wheatgrass.integration;

import org.junit.Test
import org.springframework.boot.test.TestRestTemplate

import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.rpc.config.ConfigurationService
import com.derby.nuke.wheatgrass.rpc.log.LogService
import com.derby.nuke.wheatgrass.wechat.service.UserDownloadService;
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
	
	@Test
	void downloadUsers(){
		JsonRpcHttpClient client = new JsonRpcHttpClient(new URL("http://127.0.0.1:8080/user.ci"));
		
		UserDownloadService service = ProxyUtil.createClientProxy(
			getClass().getClassLoader(),
			UserDownloadService.class,
			client);
		println service.downloadUsers();
	}
	
	@Test
	void migarateUser(){
		def client = new TestRestTemplate("nuke", "nuke.123");
		def obj = client.getForObject("http://127.0.0.1:8080/api/repository/user", Map);
		def users = [];
		obj._embedded.users.each {item->
			users.add(client.getForObject(item._links.self.href, User));
		}
		println users;
//		def client = HttpClients.createDefault();
//		
//		def response = client.execute(RequestBuilder.get("http://127.0.0.1:8080/api/repository/user").build());
//		def obj = new ObjectMapper().readValue(response.getEntity().getContent(), Map.class);
//		obj._embedded.users.each {
//			
//		}
	}
	
	def fetch(url, clazz){
		def client = new TestRestTemplate("nuke", "nuke.123");
		def map = client.getForObject(url, Map);
		map._links.each {key, value->
			if(key != "self" && key != "self"){
				
			}
		}
	}

}
