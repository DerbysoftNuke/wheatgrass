package com.derby.nuke.wheatgrass;

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import com.derby.nuke.wheatgrass.wechat.LoginInterceptor

@Configuration
class WebConfiguration extends WebMvcConfigurerAdapter {
	
	@Bean
	public LoginInterceptor openIdInterceptor() {
		return new LoginInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(openIdInterceptor());
	}
	
}