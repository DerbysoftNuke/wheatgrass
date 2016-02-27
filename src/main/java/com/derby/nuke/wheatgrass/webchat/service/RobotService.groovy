package com.derby.nuke.wheatgrass.webchat.service

import javax.xml.bind.JAXBContext

import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import com.derby.nuke.wheatgrass.webchat.model.Message
import com.derby.nuke.wheatgrass.webchat.model.MessageContext

@Service
class RobotService{

	@Value('${wechat.robot.url}')
	def url = "http://www.tuling123.com/openapi/wechatapi?key=0d70ae98cc514e4e073f828629920685";
	def marshaller = JAXBContext.newInstance(Message.class).createMarshaller();
	def unmarshaller = JAXBContext.newInstance(Message.class).createUnmarshaller();

	private def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();

	def invoke(params){
		def xmlRequest = new StringWriter();
		marshaller.marshal(MessageContext.get().inputMessage, xmlRequest);
		def response = client.execute(RequestBuilder.post().setUri(url).setEntity(new StringEntity(xmlRequest.toString(), "application/json", "UTF-8")).build());
		return unmarshaller.unmarshal(response.getEntity().getContent());
	}
	
}