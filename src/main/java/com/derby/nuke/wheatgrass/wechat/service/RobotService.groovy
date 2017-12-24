package com.derby.nuke.wheatgrass.wechat.service

import com.derby.nuke.wheatgrass.wechat.model.Message
import com.derby.nuke.wheatgrass.wechat.model.MessageContext
import org.apache.commons.io.IOUtils
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.HttpClients
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

import javax.xml.bind.JAXBContext

@Service
class RobotService {

    def Logger logger = LoggerFactory.getLogger("http.StreamLog.robot");

    @Value('${wechat.robot.url}')
    def url = "http://www.tuling123.com/openapi/wechatapi?key=0d70ae98cc514e4e073f828629920685";
    def marshaller = JAXBContext.newInstance(Message.class).createMarshaller();
    def unmarshaller = JAXBContext.newInstance(Message.class).createUnmarshaller();

    def invoke(params) {
        def xmlRequest = new StringWriter();
        marshaller.marshal(MessageContext.get().inputMessage, xmlRequest);
        logger.info("Send request >| {}", xmlRequest.toString());
        def client = HttpClients.custom().setDefaultCookieStore(new BasicCookieStore()).build();
        try {
            def response = client.execute(RequestBuilder.post().setUri(url).setEntity(new StringEntity(xmlRequest.toString(), "text/xml", "UTF-8")).build());
            def xmlReponse = IOUtils.toString(response.getEntity().content)
            logger.info("Receive response <| {}", xmlReponse.toString());
            return unmarshaller.unmarshal(new StringReader(xmlReponse));
        } finally {
            client.close();
        }
    }

}