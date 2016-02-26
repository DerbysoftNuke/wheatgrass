package com.derby.nuke.wheatgrass

import java.time.LocalDate

import org.junit.Assert
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
		User user = new User();
		user.birthday = LocalDate.now();
		user.email = "test@test.com";
		user.name = "test";
		user.openId = "openId";
		
		userRepository.save(user);
		Assert.assertNotNull(user.id);
		
		User dbUser = userRepository.findOne(user.id);
		println user;
		Assert.assertTrue(user == dbUser);
	}
}
