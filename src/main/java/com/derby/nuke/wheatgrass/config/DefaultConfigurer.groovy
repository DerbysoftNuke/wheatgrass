package com.derby.nuke.wheatgrass.config;

import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.PropertySourcesPropertyResolver
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.FileSystemResource

import com.derby.nuke.wheatgrass.log.LogbackInitializing

class DefaultConfigurer extends PropertySourcesPlaceholderConfigurer {

	def Properties configProperties = new Properties();
	def configResource;

	DefaultConfigurer(String applicationKey){
		this(applicationKey, "config.properties");
	}

	DefaultConfigurer(String applicationKey, String fileName){
		def configPath = System.getProperty(applicationKey);
		if(configPath == null){
			throw new IllegalArgumentException("Please use -D${applicationKey}=<your config path> to set config.properties");
		}

		configResource = new FileSystemResource(configPath+File.separator+fileName);
		configProperties.load(new InputStreamReader(new FileInputStream(configResource.getPath()), "UTF-8"));
		setLocations(new ClassPathResource("application.properties"), configResource);
	}
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		super.postProcessBeanFactory(beanFactory);
		new LogbackInitializing(this.getAppliedPropertySources());
		logger.info("Initizlied Logback done");
	}
	
	void setProperty(String name, String value){
		configProperties.setProperty(name, value);
		configProperties.store(new OutputStreamWriter(new FileOutputStream(configResource.getPath()), "UTF-8"), value);
	}
	
}
