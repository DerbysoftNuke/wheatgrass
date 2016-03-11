package com.derby.nuke.wheatgrass;

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import com.derby.nuke.wheatgrass.wechat.OAuthInterceptor

@Configuration
class WebConfiguration extends WebMvcConfigurerAdapter {
	
	@Bean
	public OAuthInterceptor loginInterceptor() {
		return new OAuthInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor());
	}
	
}