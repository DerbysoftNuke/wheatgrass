package com.derby.nuke.wheatgrass

import java.time.LocalDate

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
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
	@Autowired
	JavaMailSender javaMailSender;

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
	
	@Test
	void sendMail(){
		SimpleMailMessage message = new SimpleMailMessage();
		message.from = "passyt@gmail.com";
		message.to = "passyt@qq.xom";
		message.subject = "test";
		message.text= "test";
		javaMailSender.send(message);
	}
}
