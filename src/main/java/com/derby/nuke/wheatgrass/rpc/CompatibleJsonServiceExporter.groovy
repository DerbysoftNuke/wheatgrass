package com.derby.nuke.wheatgrass.rpc;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

import com.googlecode.jsonrpc4j.JsonRpcServer;
import com.googlecode.jsonrpc4j.spring.AbstractJsonServiceExporter;

class CompatibleJsonServiceExporter extends AbstractJsonServiceExporter implements HttpRequestHandler {

	private CompatibleJsonRpcServer jsonRpcServer;

	@Override
	protected void exportService() {
		jsonRpcServer = new CompatibleJsonRpcServer(getObjectMapper(), getProxyForService(), getServiceInterface());
//		jsonRpcServer.setBackwardsComaptible(backwardsComaptible);
//		jsonRpcServer.setRethrowExceptions(rethrowExceptions);
//		jsonRpcServer.setAllowExtraParams(allowExtraParams);
//		jsonRpcServer.setAllowLessParams(allowLessParams);
//		jsonRpcServer.setExceptionLogLevel(exceptionLogLevel);
//		jsonRpcServer.setInvocationListener(invocationListener);
	}

	/**
	 * {@inheritDoc}
	 */
	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		jsonRpcServer.handle(request, response);
		response.getOutputStream().flush();
	}

}
