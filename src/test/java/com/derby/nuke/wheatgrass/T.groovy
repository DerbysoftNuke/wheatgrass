import org.apache.commons.codec.digest.DigestUtils

import com.google.common.base.Joiner
import com.qq.weixin.mp.aes.WXBizMsgCrypt

def encodingAesKey = "CYachmMI7ut3JbvnjK7yIovrtKZ6Bn8o02XTHDv9iqI";
def token = "test";
def appId  = "wx6cf7c599aa792ce2";
def timestamp = "1457677218";
def nonce = "30044840"
def signature = "8e3789039f2acc20d8223e87ae35ef2c2b93ef72";
def array = [token, timestamp, nonce, ];
def echoStr = URLDecoder.decode("zvip8jfaa4KnQ0cGds8V7aqyi3m2PuMVuF7QN6ldbx0%2F6oBFLntunrKxPRcjiYgNQhqYPUSl2lrUde7DgUG59g%3D%3D", "UTF-8");

Collections.sort(array);
def string = Joiner.on("").join(array);
def calSignature = DigestUtils.sha1Hex(string);
println string;
println calSignature;

def xml = '''
<xml>
	<ToUserName><![CDATA[wx6cf7c599aa792ce2]]></ToUserName>
	<Encrypt><![CDATA[RX8v4FIVw3s5MzNvu+eNHjUQkobC/VvJJk6Wph7sgK1P3RxfI7KfdyER6pZ3I2Q0W8+XLWwYnzysPI1jEF1xz3YArRe3E58eacCHBgWKfP+ar/03oC30WEW70WxIhtOz65GYlxdzQHplHS0oli5twCEKSvBl6rizx5ozljnC2SrPTisTOZ1OWMmiQ/7QaH2uI+F+Cq6ZPD+Fzk8wWLXmfFC8A0AlT43SxLSDu7DTfmXozDbV1pou0bLM9c0dtY0VRxKIqT7DVxh9uAVsmVbM0jo0yO166gWgcaCuw9TdOgWDAOuw4y624JuFbpkV7gSNvGYGAaUf4WKxNcqYeHNLGl1JWKM/zYorvIDOArt0PL06kilfQA8Fk4E8gJJ/7HdopHvJOby+0DekezELw2v03Do+4WtUB+usOGM5GgsdJ+YBoOSPmVnPhqzyfHjA5x5wKz1GwOZlMW3kpXQlsZzwaw==]]></Encrypt>
	<AgentID><![CDATA[0]]></AgentID>
</xml>
'''

WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
println pc.decryptMsg(signature, timestamp, nonce, xml.trim());
//println pc.verifyUrl(signature, timestamp, nonce, echoStr);
//println pc.decryptMsg(signature, timestamp, nonce, encryptedMessage);
//String mingwen = pc.encryptMsg(replyMsg, timestamp, nonce);