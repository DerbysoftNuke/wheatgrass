package com.derby.nuke.wheatgrass;

import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = DefaultApplication)
@WebIntegrationTest(randomPort = true)
class IntegrationUnitTest {
	
	@Value('${local.server.port}')
	int port;
	
	static{
		def url = IntegrationUnitTest.class.getResource("config.properties");
		def path = new File(url.getPath()).getParent()
		System.setProperty("nuke.wheatgrass", path);
	}
	
	def getUrl(uri){
		return "http://127.0.0.1:${port}/${uri}";
	}

}
