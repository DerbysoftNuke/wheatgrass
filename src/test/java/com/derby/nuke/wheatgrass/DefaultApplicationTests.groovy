package com.derby.nuke.wheatgrass

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner
import org.springframework.test.context.web.WebAppConfiguration

import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository

@RunWith(SpringJUnit4ClassRunner)
@SpringApplicationConfiguration(classes = DefaultApplication)
@WebAppConfiguration
class DefaultApplicationTests {
	
	@Autowired
	UserRepository userRepository;

	@Test
	void contextLoads() {
		User user = userRepository.findOne("8a4818ac531be72b01531be746f70000");
		println user.email;
	}
}
