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
		try {
			Server.readFile(request, response);
		} catch (IOException e) {
			response.setCode(404);
			response.setCodeString("Not Found");
			response.setBody("Could not find file");
		}
		response.getHeaders().put("Date", Server.getHTTPTimestamp());
		response.getHeaders().put("Connection", "close");
		response.getHeaders().put("Server", "TinyHTTPServer");
		String s = request.getPath().getPath();
		if (s.endsWith(".html"))
			response.getHeaders().put("Content-Type", "text/html");
		else if (s.endsWith(".png"))
			response.getHeaders().put("Content-Type", "image/png");
		else
			response.getHeaders().put("Content-Type", "text/plain");
		return response;
	}

}
