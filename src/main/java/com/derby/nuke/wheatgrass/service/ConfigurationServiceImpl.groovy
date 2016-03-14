package com.derby.nuke.wheatgrass.service;

import java.util.Map;

import org.springframework.stereotype.Service

@Service
class ConfigurationServiceImpl implements ConfigurationService {

	@Override
	String getProperty(String key){
	}

	@Override
	Map<String, String> getAll() {
		return ["test":"123"];
	}

	@Override
	void setProperty(String key, String value){
	}

	@Override
	void setProperties(Map<String, String> properties){
	}
}
