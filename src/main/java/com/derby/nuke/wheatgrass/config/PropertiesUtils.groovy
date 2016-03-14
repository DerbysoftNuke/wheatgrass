package com.derby.nuke.wheatgrass.config

import org.springframework.core.env.AbstractEnvironment
import org.springframework.core.env.PropertyResolver
import org.springframework.core.env.PropertySources

class PropertiesUtils {
	
	static Map<String,String> getProperties(PropertySources sources){
		def result = [:];
		sources.each{item->
			def source = item.getSource();
			if(source instanceof Map){
				result.putAll(source);
			}else if(source instanceof AbstractEnvironment){
				result.putAll(getProperties(source.getPropertySources()));
			}
		}
		return result;
	}

	static Set<String> getKeys(PropertySources properties, String prefix) {
		Set<String> keys = new HashSet<String>();
		properties.each{item->
			def source = item.getSource();
			if(source instanceof Map){
				source.each {key, value->
					if(key.startsWith(prefix)) {
						keys.add(key);
					}
				}
			}else if(source instanceof AbstractEnvironment){
				keys.addAll(getKeys(source.getPropertySources(), prefix));
			}
		}
		return keys;
	}
	
	static List<String> getList(PropertyResolver resolver, String key) {
		if(!resolver.containsProperty(key)){
			return null;
		}
		
		def stringValue = resolver.getProperty(key);
		List<String> values = new ArrayList<String>();
		values.addAll(Arrays.asList(stringValue.split(",")));
		return values;
	}
}
