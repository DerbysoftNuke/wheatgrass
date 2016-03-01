package com.derby.nuke.wheatgrass.wechat;

import java.util.regex.Pattern

import javax.mail.internet.MimeUtility
import javax.xml.bind.JAXBContext

import org.apache.commons.codec.digest.DigestUtils
import org.codehaus.groovy.runtime.InvokerHelper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.http.MediaType
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.ModelAndView
import org.yaml.snakeyaml.Yaml

import com.derby.nuke.wheatgrass.entity.User
import com.derby.nuke.wheatgrass.repository.UserRepository
import com.derby.nuke.wheatgrass.wechat.model.Message
import com.derby.nuke.wheatgrass.wechat.model.MessageContext
import com.derby.nuke.wheatgrass.wechat.model.Message.MessageType
import com.derby.nuke.wheatgrass.wechat.service.WechatService
import com.google.common.base.Joiner

@RestController
@RequestMapping("/wechat")
class MessageController implements ApplicationContextAware {

	private static final def log = LoggerFactory.getLogger(MessageController.class);

	@Value('${wechat.token}')
	def token;
	@Value('${wechat.message.verify}')
	def boolean verify = false;
	@Value('${web.external.url}')
	def externalUrl;
	def ApplicationContext applicationContext;
	@Autowired
	def UserRepository userRepository;
	@Autowired
	def JavaMailSender mailSender;
	@Autowired
	def WechatService wechatService;

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
			
			def user = userRepository.getByOpenId(message.from);
			if(user == null){
				message.type = MessageType.text;
				message.content = "请<a href='${externalUrl}/wechat/bind?openId=${from}'>绑定邮箱</a>";
			}else{
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
	
	@RequestMapping(value="/bind_email", method = RequestMethod.GET)
	def bindEmail(@RequestParam(value="code", required=false) code,@RequestParam(value="openId", required=false) openId, Model model){
		if(code == null && openId == null){
			throw new IllegalArgumentException("Invalid url");
		}
		
		if(code != null){
			openId = wechatService.getOpenId(code);
		}
		
		if(openId == null){
			throw new IllegalArgumentException("Invalid open id ${openId}");
		}
		
		def user = userRepository.getByOpenId(openId);
		if(user != null){
			if(user.validation){
				return new ModelAndView("wechat/warning", "message", "邮箱已激活: ${user.email}");
			}
		}
		
		model.addAttribute("openId", openId);
		return new ModelAndView("wechat/bind_email");
	}
	
	@RequestMapping(value="/bind_email", method = RequestMethod.POST)
	def bindEmail(@RequestParam(value="openId") openId, @RequestParam(value="emailPrefix") emailPrefix, @RequestParam(value="emailSufix") emailSufix){
		def user = userRepository.getByOpenId(openId);
		if(user != null){
			if(user.validation){
				return new ModelAndView("wechat/warning", "message", "邮箱已经绑定: ${user.email}");
			}
		}
		
		def email = emailPrefix+emailSufix;
		def token = UUID.randomUUID().toString();
		
		def emailUser = userRepository.getByEmail(email);
		if(emailUser != null){
			return new ModelAndView("wechat/warning", "message", "邮箱地址已经被使用!");
		}
		
		if(user == null){
			user = new User(email:email, token:token, openId:openId);
		}else{
			user.email = email;
			user.token = token;
			user.openId = openId;
		}
		
		def message = new SimpleMailMessage();
		message.from = "nuke.wiki@derbygroupmail.com";
		message.to = new String[1];
		message.to[0] = email;
		message.subject = MimeUtility.encodeText("激活邮箱", "UTF-8", "B");
		message.text= "${externalUrl}/wechat/verify_email?token=${token}&email=${email}";
		mailSender.send(message);
		userRepository.save(user);
		return new ModelAndView("wechat/successful", "message", "请前往邮箱激活!");
	}
	
	@RequestMapping(value="/verify_email")
	def verifyEmail(@RequestParam(value="token") token, @RequestParam(value="email") email){
		def user = userRepository.getByEmail(email);
		if(user == null){
			throw new IllegalArgumentException("不存在的邮箱地址, 请重新绑定邮箱");
		}
		
		if(user.validation){
			return new ModelAndView("wechat/warning", "message", "邮箱已经激活");
		}
		
		if(user.token != token){
			throw new IllegalArgumentException("无效请求, 请重新绑定邮箱");
		}
		
		user.validation = true;
		userRepository.save(user);
		return new ModelAndView("wechat/successful", "message", "激活成功!");
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