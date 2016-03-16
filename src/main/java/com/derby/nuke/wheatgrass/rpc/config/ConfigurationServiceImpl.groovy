package com.derby.nuke.wheatgrass.rpc.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service

import com.derby.nuke.wheatgrass.config.DefaultConfigurer

@Service
class ConfigurationServiceImpl implements ConfigurationService {
	
	@Autowired
	def DefaultConfigurer configurer;

	@Override
	String getProperty(String key){
		return configurer.getProperty(key);
	}

	@Override
	Map<String, String> getAll() {
		return configurer.getProperties();
	}

	@Override
	void setProperty(String key, String value){
		configurer.setProperty(key, value);
	}

}
