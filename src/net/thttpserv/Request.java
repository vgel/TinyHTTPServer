package net.thttpserv;

import java.net.Socket;
import java.net.URI;
import java.util.HashMap;

public class Request {
	Socket source;
	String method;
	URI path;
	String httpVersion;
	String body;
	HashMap<String, String> headers = new HashMap<String, String>();
	
	public Request(Socket source, String method, URI path, String httpVersion) {
		this.source = source;
		this.method = method;
		this.path = path;
		this.httpVersion = httpVersion;
	}
	
	public String toString(){
		return source.getInetAddress() + " made " + method + " " + path + " " + httpVersion;
	}
	/**
	 * @return the source
	 */
	public Socket getSource() {
		return source;
	}
	/**
	 * @return the method
	 */
	public String getMethod() {
		return method;
	}
	/**
	 * @return the path
	 */
	public URI getPath() {
		return path;
	}
	/**
	 * @return the httpVersion
	 */
	public String getHttpVersion() {
		return httpVersion;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * @return the headers
	 */
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	/**
	 * @param source the source to set
	 */
	public void setSource(Socket source) {
		this.source = source;
	}

	/**
	 * @param method the method to set
	 */
	public void setMethod(String method) {
		this.method = method;
	}

	/**
	 * @param path the path to set
	 */
	public void setPath(URI path) {
		this.path = path;
	}

	/**
	 * @param httpVersion the httpVersion to set
	 */
	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}
	
}