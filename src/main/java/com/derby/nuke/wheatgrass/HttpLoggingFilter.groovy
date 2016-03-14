package com.derby.nuke.wheatgrass;

import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.FilterConfig
import javax.servlet.ServletException
import javax.servlet.ServletInputStream
import javax.servlet.ServletOutputStream
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletRequestWrapper
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletResponseWrapper

import org.apache.commons.io.input.TeeInputStream
import org.apache.commons.io.output.TeeOutputStream
import org.slf4j.LoggerFactory

import com.derby.nuke.wheatgrass.io.DelegatingServletInputStream
import com.derby.nuke.wheatgrass.io.DelegatingServletOutputStream

class HttpLoggingFilter implements Filter {
	
	private static final log = LoggerFactory.getLogger(HttpLoggingFilter.class);

	@Override
	void init(FilterConfig filterConfig) throws ServletException {
		log.debug("Init HttpLoggingFilter");
	}

	@Override
	void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		def uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
		ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
		ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
		try{
			chain.doFilter(new HttpServletRequestWrapper(request){
				
				@Override
				ServletInputStream getInputStream() throws IOException {
					return new DelegatingServletInputStream(new TeeInputStream(super.getInputStream(), requestStream));
				}
				
				@Override
				BufferedReader getReader() throws IOException {
					return new BufferedReader(new DelegatingServletInputStream(new TeeInputStream(super.getInputStream(), requestStream)));
				}
				
			}, new HttpServletResponseWrapper((HttpServletResponse) response) {
				@Override
				public ServletOutputStream getOutputStream() throws IOException {
					return new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), responseStream));
				}
	
				@Override
				public PrintWriter getWriter() throws IOException {
					return new PrintWriter(new DelegatingServletOutputStream(new TeeOutputStream(super.getOutputStream(), responseStream)));
				}
			});
		}finally{
			logRequest(request, requestStream, uuid);
			logResponse(response, responseStream, uuid);
		}
	}

	@Override
	void destroy() {
		log.debug("destroy HttpLoggingFilter");
	}

	void logRequest(HttpServletRequest request, ByteArrayOutputStream requestStream, uuid) throws IOException {
		def log = LoggerFactory.getLogger("http.StreamLog");
		log.info("{} Receive request <| {}", uuid, requestStream.toString("UTF-8"));
	}

	void logResponse(HttpServletResponse response, ByteArrayOutputStream responseStream, uuid) {
		def log = LoggerFactory.getLogger("http.StreamLog");
		log.info("{} Send response >| {}", uuid, responseStream.toString("UTF-8"));
	}
}
