package com.derby.nuke.wheatgrass.wechat.controller;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClients
import org.junit.Assert;
import org.junit.Test
import org.springframework.beans.factory.annotation.Value

import com.derby.nuke.wheatgrass.IntegrationUnitTest
import com.qq.weixin.mp.aes.SHA1;
import com.qq.weixin.mp.aes.WXBizMsgCrypt

class MessageControllerTest extends IntegrationUnitTest {

	@Value('${wechat.app.id}')
	def appId;
	@Value('${wechat.token}')
	def token;
	@Value('${wechat.app.encoding.aes.key}')
	def encodingAesKey;
	def client = HttpClients.custom().build();

	@Test
	void test() {
		WXBizMsgCrypt tool = new WXBizMsgCrypt(token, encodingAesKey, appId);
		
		def timestamp = String.valueOf(System.currentTimeMillis());
		def nonce = "nonce";
		def encryptMsg = "2HDmqAJNdvhSvr88EjojA/YVULfl5LqsI/8WDh7b/uhMoKSkvhjddtpPO4qMuPl5Qhq95lm8mw1Ns2ljyOrp5K2zsiJ42PGqnGfoDXbAQs3gIyKmnFgyQSiMehavVgs0Xo04maA1+RzzyuYR1zxjhrD1cHmsY/j2YG47xphIUBp/5J4PT6uAYvLRhHHguUYYH1hJaiZs13I4KUjfw7BRDqFFJjVsENhUNxHKj49x7yIeBepIr6EdGrkiOkl0+h/7AypOaap3iX46q8BX5qyIOTkLTGC32UldrIPwAMsq1k4=";;
		def signature = SHA1.getSHA1(token, timestamp, nonce, encryptMsg);
		
		def request = "<xml><ToUserName><![CDATA[test]]></ToUserName><Encrypt><![CDATA[${encryptMsg}]]></Encrypt><MsgSignature><![CDATA[${signature}]]></MsgSignature><TimeStamp>${timestamp}</TimeStamp><Nonce><![CDATA[${nonce}]]></Nonce></xml>";
		
		def response = client.execute(RequestBuilder.post(getUrl("wechat")).addHeader("Content-Type", "text/xml;charset=utf-8").addParameter("msg_signature", signature).addParameter("timestamp", timestamp).addParameter("nonce", nonce).setEntity(new StringEntity(request, "UTF-8")).build());
		println IOUtils.toString(response.getEntity().getContent(), "UTF-8");
	}
}