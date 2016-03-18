package com.derby.nuke.wheatgrass.wechat.service;

import com.googlecode.jsonrpc4j.JsonRpcService

@JsonRpcService("/user.ci")
interface UserDownloadService {

	void downloadUsers();

	void downloadUser(userId);
}
