package com.derby.nuke.wheatgrass

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

import com.derby.nuke.wheatgrass.DefaultApplication;

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = DefaultApplication)
@WebAppConfiguration
class DefaultApplicationTests {

	@Test
	void contextLoads() {
	}
}
