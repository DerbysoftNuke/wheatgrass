package com.derby.nuke.wheatgrass.webchat;

import java.util.regex.Pattern

import javax.xml.bind.JAXBContext

import org.apache.commons.codec.digest.DigestUtils
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.yaml.snakeyaml.Yaml

import com.derby.nuke.wheatgrass.webchat.model.Message
import com.derby.nuke.wheatgrass.webchat.model.MessageContext
import com.google.common.base.Joiner

@RestController
@RequestMapping("/wechat")
class MessageController implements ApplicationContextAware {

	private static final def log = LoggerFactory.getLogger(MessageController.class);

	@Value('${wechat.token}')
	def token;
	@Value('${wechat.message.verify}')
	def boolean verify = false;
	def ApplicationContext applicationContext;

	@RequestMapping(method = RequestMethod.GET)
	def valid(@RequestParam signature, @RequestParam timestamp, @RequestParam nonce, @RequestParam echostr){
		def array = [token, timestamp, nonce];
		Collections.sort(array);
		def string = Joiner.on("").join(array);
		def calSignature = DigestUtils.sha1Hex(string);
		log.info("InputSignature: ${signature} <=> OutputSignature:${calSignature}: timestamp=> ${timestamp}, nonce=> ${nonce}, echostr=>${echostr}");
		if(signature == calSignature){
			return echostr;
		}

		return "";
	}

	@RequestMapping(method = RequestMethod.POST, produces=MediaType.TEXT_XML_VALUE)
	def Message receive(@RequestParam(required=false) signature, @RequestParam(required=false) timestamp, @RequestParam(required=false) nonce, @RequestParam(value="encrypt_type", required=false) encryptType, @RequestParam(value="msg_signature", required=false) msgSignature, @RequestBody Message message){
		if(log.isInfoEnabled()){
			def writer = new StringWriter();
			JAXBContext.newInstance(Message.class).createMarshaller().marshal(message, writer);
			log.info("Receive message: ${writer}");
		}

		if(verify){
			def echostr = "123";
			if(echostr != valid(signature, timestamp, nonce, echostr){
				throw new IllegalArgumentException("Invalid signature");
			}
		}

		try{
			MessageContext.put(new MessageContext(userId: message.from, inputMessage: message));
			def from = message.from;
			def to = message.to;
	
			Yaml yaml = new Yaml();
			def configuration = yaml.load(new InputStreamReader(MessageController.class.getClassLoader().getResourceAsStream("wechat.yaml"),"UTF-8"));
			def handler = find(configuration.handlers, message);
			def result = [type: "text", content: "请输入帮助命令: help"];
			if(handler != null){
				result = invoke(message, handler.params, handler.service);
			}
	
			if(result.properties != null){
				InvokerHelper.setProperties(message, result.properties);
			}else{
				result.each{name,value->
					message[name] = value;
				}
			}
			
			message.from = to;
			message.to = from;
	
			if(log.isInfoEnabled()){
				def writer = new StringWriter();
				JAXBContext.newInstance(Message.class).createMarshaller().marshal(message, writer);
				log.info("Return message: ${writer}");
			}
			return message;
		}finally{
			MessageContext.remove();
		}
	}

	def invoke(message, params, serviceText){
		def beanAndMethods = serviceText.trim().split("\\.");
		def bean = applicationContext.getBean(beanAndMethods[0]);
		def result = [:];
		if(beanAndMethods.length >= 2){
			result = bean."${beanAndMethods[1]}"(params);
		}else{
			result = bean.invoke(params);
		}
		return result;
	}

	def find(handlers, message){
		def params = [];
		def handler = handlers.find{item->
			boolean notMatch = false;
			for(entry in item.matchers.entrySet()){
				def name = entry.key;
				def patternText = entry.value;
				if(message[name] == null && patternText == "null"){
					continue;
				}
				
				def pattern = Pattern.compile(patternText);
				def matcher = pattern.matcher(String.valueOf(message[name]));
				if(!matcher.matches()){
					notMatch = true;
					params = [];
					break;
				}

				for(int i = 0;i<matcher.groupCount();i++){
					params.add(matcher.group(i+1));
				}
			}

			if(!notMatch){
				return true;
			}
			return false;
		}

		if(handler == null){
			return null;
		}

		return [params: params, service: handler.service];
	}

	@Override
	public void setApplicationContext(ApplicationContext context){
		applicationContext = context;
	}
}