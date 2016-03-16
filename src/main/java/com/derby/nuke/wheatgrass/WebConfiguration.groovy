package com.derby.nuke.wheatgrass;

import javax.servlet.DispatcherType

import org.springframework.boot.context.embedded.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.FileSystemResource
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter

import com.derby.nuke.wheatgrass.config.DefaultConfigurer
import com.derby.nuke.wheatgrass.rpc.CompatibleRpcServiceExporter
import com.derby.nuke.wheatgrass.wechat.OAuthInterceptor

@Configuration
class WebConfiguration extends WebMvcConfigurerAdapter {

	@Bean
	public HttpLoggingFilter httpLoggingFilter() {
		HttpLoggingFilter filter = new HttpLoggingFilter();
		filter.setIncludePayload(true);
		filter.setIncludeClientInfo(false);
		filter.setIncludeQueryString(true);
		filter.setBeforeMessagePrefix("");
		filter.setBeforeMessageSuffix("");
		filter.setAfterMessagePrefix("");
		filter.setAfterMessageSuffix("");
		filter.setMaxPayloadLength(Integer.MAX_VALUE);
		return filter;
	}

	@Bean
	public OAuthInterceptor loginInterceptor() {
		return new OAuthInterceptor();
	}

	@Bean
	public CompatibleRpcServiceExporter autoJsonRpcServiceExporter(){
		return new CompatibleRpcServiceExporter();
	}

	@Bean
	public DefaultConfigurer externalConfigurer(){
		return new DefaultConfigurer("nuke.wheatgrass");
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginInterceptor());
	}
}