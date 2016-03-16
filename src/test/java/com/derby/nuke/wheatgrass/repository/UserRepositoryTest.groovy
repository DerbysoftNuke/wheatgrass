package com.derby.nuke.wheatgrass.repository

import java.time.LocalDate

import org.junit.Assert
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

import com.derby.nuke.wheatgrass.IntegrationUnitTest
import com.derby.nuke.wheatgrass.entity.User

class UserRepositoryTest extends IntegrationUnitTest {
	
	@Autowired
	UserRepository userRepository;
	
	@Test
	@Transactional
	void test() {
		User user = new User();
		user.birthday = LocalDate.now();
		user.email = "test@test.com";
		user.name = "test";
		user.userId = "userId";

		userRepository.save(user);
		Assert.assertNotNull(user.id);

		User dbUser = userRepository.findOne(user.id);
		Assert.assertTrue(user == dbUser);
		
		dbUser = userRepository.getByEmail(user.email);
		Assert.assertTrue(user == dbUser);
		
		dbUser = userRepository.getByUserId(user.userId);
		Assert.assertTrue(user == dbUser);
	}

}
