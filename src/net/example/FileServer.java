package net.example;

import java.io.IOException;
import java.net.Socket;

import net.thttpserv.ConnectionHandler;
import net.thttpserv.Request;
import net.thttpserv.Response;
import net.thttpserv.Server;
import net.thttpserv.HandlerCreator;

public class FileServer extends ConnectionHandler {

	public FileServer(Socket s) {
		super(s);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Server.setFactory(new FileServerFactory());
		Server.main(args);
	}
	
	public Response handleGET(Request request, Response response){
		Server.log(2, "FileServer handling GET");
		response.setCode(200);
		response.setCodeString("OK");
		String mimeType = "text/plain";
		try {
			mimeType = Server.readFile(request, response);
		} catch (IOException e) {
			response.setCode(404);
			response.setCodeString("Not Found");
			response.setBody("Could not find file");
		}
		response.getHeaders().put("Date", Server.getHTTPTimestamp());
		response.getHeaders().put("Connection", "close");
		response.getHeaders().put("Server", "TinyHTTPServer");
		response.getHeaders().put("Content-Type", mimeType);
		return response;
	}

}
