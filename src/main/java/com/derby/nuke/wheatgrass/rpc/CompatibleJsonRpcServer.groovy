package com.derby.nuke.wheatgrass.rpc;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.googlecode.jsonrpc4j.JsonRpcServer;

class CompatibleJsonRpcServer extends JsonRpcServer {

	public CompatibleJsonRpcServer(ObjectMapper mapper, Object handler, Class<?> remoteInterface) {
		super(mapper, handler, remoteInterface);
	}

	@Override
	public int handleObject(ObjectNode node, OutputStream ops) throws IOException {
		JsonNode idNode = node.get("id");
		if (idNode == null || idNode.isNull()) {
			node.set("id", TextNode.valueOf(UUID.randomUUID().toString()));
		}

		return super.handleObject(node, ops);
	}
	
	@Override
	protected ObjectNode createErrorResponse(String jsonRpc, Object id, int code, String message, Object data) {
		def repsone = super.createErrorResponse(jsonRpc, id, code, message, data);
		def error = repsone.get("error");
		error.put("type", code);
		return response;
	}

}
