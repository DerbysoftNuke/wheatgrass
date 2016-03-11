package com.derby.nuke.wheatgrass.wechat.controller;

import java.util.regex.Pattern

import javax.xml.bind.JAXBContext
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.apache.commons.codec.digest.DigestUtils
import org.codehaus.groovy.runtime.InvokerHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.xml.sax.InputSource
import org.yaml.snakeyaml.Yaml

import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.OAuthRequired
import com.derby.nuke.wheatgrass.wechat.model.Message
import com.derby.nuke.wheatgrass.wechat.model.MessageContext
import com.derby.nuke.wheatgrass.wechat.model.Message.MessageType
import com.google.common.base.Joiner
import com.qq.weixin.mp.aes.WXBizMsgCrypt

@RestController
@OAuthRequired(false)
class MessageController extends WechatController implements ApplicationContextAware {

	@Value('${wechat.app.id}')
	def appId;
	@Value('${wechat.token}')
	def token;
	@Value('${wechat.app.encoding.aes.key}')
	def encodingAesKey;
	@Value('${web.external.url}')
	def externalUrl;
	@Autowired
	def ApplicationContext applicationContext;

	@RequestMapping(method = RequestMethod.GET)
	def valid(@RequestParam(value="msg_signature") msgSignature, @RequestParam timestamp, @RequestParam nonce, @RequestParam echostr){
		WXBizMsgCrypt tool = new WXBizMsgCrypt(token, encodingAesKey, appId);
		return tool.verifyUrl(msgSignature, timestamp, nonce, echostr);
	}

	@RequestMapping(method = RequestMethod.POST, produces=MediaType.TEXT_XML_VALUE)
	def Message receive(@RequestParam(value="msg_signature") msgSignature, @RequestParam timestamp, @RequestParam nonce, @RequestBody String request){
		if(log.isDebugEnabled()){
			log.debug("Receive request <<| ${request}");
		}
		
		WXBizMsgCrypt tool = new WXBizMsgCrypt(token, encodingAesKey, appId);
		def requestMessage = tool.decryptMsg(msgSignature, timestamp, nonce, request);
		if(log.isInfoEnabled()){
			log.info("Receive message <<| ${requestMessage}");
		}

		def Message message = JAXBContext.newInstance(Message.class).createUnmarshaller().unmarshal(new StringReader(requestMessage));
		try{
			MessageContext.put(new MessageContext(userId: message.from, inputMessage: message));
			def from = message.from;
			def to = message.to;

			if(message.type != null){
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
			}

			message.from = to;
			message.to = from;
			
			def writer = new StringWriter();
			JAXBContext.newInstance(Message.class).createMarshaller().marshal(message, writer);
			def responseMessage = writer.toString();
			if(log.isInfoEnabled()){
				log.info("Return message >>| ${responseMessage}");
			}
			def response = tool.encryptMsg(responseMessage, timestamp, nonce);
			if(log.isDebugEnabled()){
				log.debug("Return response >>| ${response}");
			}
			
			return response;
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
}