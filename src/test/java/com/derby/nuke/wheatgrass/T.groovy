import org.apache.commons.codec.digest.DigestUtils

import com.google.common.base.Joiner
import com.qq.weixin.mp.aes.WXBizMsgCrypt

def encodingAesKey = "CYachmMI7ut3JbvnjK7yIovrtKZ6Bn8o02XTHDv9iqI";
def token = "test";
def appId  = "wx6cf7c599aa792ce2";
def timestamp = "1457601220";
def nonce = "1502697948"
def signature = "a33f37b40179a5cc34edde58aa66986abf040427";
def array = [token, timestamp, nonce];
def echoStr = URLDecoder.decode("zvip8jfaa4KnQ0cGds8V7aqyi3m2PuMVuF7QN6ldbx0%2F6oBFLntunrKxPRcjiYgNQhqYPUSl2lrUde7DgUG59g%3D%3D", "UTF-8");

Collections.sort(array);
def string = Joiner.on("").join(array);
def calSignature = DigestUtils.sha1Hex(string);
println string;
println calSignature;

WXBizMsgCrypt pc = new WXBizMsgCrypt(token, encodingAesKey, appId);
println pc.verifyUrl(signature, timestamp, nonce, echoStr);
//println pc.decryptMsg(signature, timestamp, nonce, encryptedMessage);
//String mingwen = pc.encryptMsg(replyMsg, timestamp, nonce);