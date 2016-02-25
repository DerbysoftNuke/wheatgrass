package com.derby.nuke

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.test.context.web.WebAppConfiguration

import springboot.webchat.WebchatApplication;

import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = DefaultApplication)
@WebAppConfiguration
class DefaultApplicationTests {

	@Test
	void contextLoads() {
	}
}
