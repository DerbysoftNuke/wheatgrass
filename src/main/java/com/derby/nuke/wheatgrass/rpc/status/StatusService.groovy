package com.derby.nuke.wheatgrass.rpc.status

import com.googlecode.jsonrpc4j.JsonRpcService

@JsonRpcService("/status.ci")
interface StatusService {
	
	Set<String> keys();
	
	Map<String, String> status();
	
	Map<String, String> statusWith(Set<String> keys);
	
	Map<String, String> statusWithout(Set<String> keys);

}
