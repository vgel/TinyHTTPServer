package net.thttpserv;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.io.DataOutputStream;
import java.io.IOException;

public class Response {
	int code;
	String codeString;
	byte[] body;
	HashMap<String, String> headers = new HashMap<String, String>();
	
	public static Response createGenericResponse(int code, String reason){
		Response resp = new Response();
		resp.setCode(code);
		switch(code){
		case 100:
			resp.setCodeString("Continue");
			break;
		case 400:
			resp.setCodeString("Bad Request");
			break;
		case 404:
			resp.setCodeString("Not Found");
			break;
		case 501:
			resp.setCodeString("Not Implemented");
			break;
		case 505:
			resp.setCodeString("HTTP Version not supported");
			break;
		}
		resp.setBody(reason);
		return resp;
	}
	
	public void write(DataOutputStream out) throws IOException{
		out.writeBytes(toString());
		out.write(getBody());
		out.writeBytes("\r\n");
	}
	
	public String toString(){
		String ret = "HTTP/1.1 " + code + " " + codeString + "\r\n";
		Set<Entry<String, String>> set = headers.entrySet();
		Iterator<Entry<String, String>> iter = set.iterator();
		while(iter.hasNext()){
			Entry<String, String> ent = iter.next();
			ret += ent.getKey() + ": " + ent.getValue() + "\r\n";
		}
		ret += "\r\n";
		return ret;
	}
	
	/**
	 * @return the codeString
	 */
	public String getCodeString() {
		return codeString;
	}

	/**
	 * @param codeString the codeString to set
	 */
	public void setCodeString(String codeString) {
		this.codeString = codeString;
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
	/**
	 * @param code the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * @return the body
	 */
	public byte[] getBody() {
		return body;
	}
	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body.getBytes();
	}
	
	public void setBody(byte[] body) {
		this.body = body;
	}
	/**
	 * @return the headers
	 */
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	/**
	 * @param headers the headers to set
	 */
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
}