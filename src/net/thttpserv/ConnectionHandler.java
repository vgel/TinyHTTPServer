package net.thttpserv;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

public class ConnectionHandler implements Runnable {
	
	Socket socket;

	public ConnectionHandler(Socket s) {
		socket = s;
	}

	@Override
	public void run(){
		try{
			handleConnection(socket);
		} catch (Exception e){
			System.err.println("Error with socket " + socket.getInetAddress());
			if (Server.verbose >= 2)
				e.printStackTrace();
		}
	}
	
	private void handleConnection(Socket socket) throws IOException {
		BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		
		String line = in.readLine();
		if (line == null) return;
		String[] header = line.split(" ");
		if (header.length < 2){
			Response.createGenericResponse(400, "No HTTP line").write(out);
			Server.log(1, "Bad connection from " + socket.getInetAddress());
			return;
		}
		
		String http;
		if ("HTTP/1.1".equals(header[2])){
			http = "HTTP/1.1";
		}
		else if ("HTTP/1.0".equals(header[2])){
			http = "HTTP/1.0";
		}
		else{
			Response.createGenericResponse(505, "Too low/high HTTP version " + header[2]).write(out);
			Server.log(1, "Bad HTTP Version " + header[2] + " from " + socket.getInetAddress());
			return;
		}
		
		if (http.equals("HTTP/1.1")){
			Response.createGenericResponse(100, "").write(out);
		}
		
		Request request;
		try {
			request = new Request(socket, header[0], new URI(header[1]), http);
		} catch (URISyntaxException e) {
			Response.createGenericResponse(400, "Badly formed URL").write(out);
			return;
		}
		
		String s = in.readLine();
		while(s.contains(": ")){
			request.getHeaders().put(s.split(": ")[0].replace(": ", ""), s.split(": ")[1].replace(": ", ""));
			s = in.readLine();
		}
		Server.log(2, "%s : %s", request.toString(), request.getHeaders().toString());
		
		if (http.equals("HTTP/1.1") && !request.getHeaders().containsKey("Host")){
			Response.createGenericResponse(400, "No Host parameter").write(out);
			return;
		}
		
		Response resp = handleRequest(request);
		if (resp == null)
			resp = Response.createGenericResponse(501, "Method not supported: " + request.getMethod());
		Server.log(2, resp.toString());
		
		resp.write(out);
		out.close();
	}
	
	public Response handleRequest(Request request){
		Response response = new Response();
		if (request.getMethod().equals("GET"))
			return handleGET(request, response);
		else if (request.getMethod().equals("POST"))
			return handlePOST(request, response);
		else if (request.getMethod().equals("PUT"))
			return handlePUT(request, response);
		else if (request.getMethod().equals("DELETE"))
			return handleDELETE(request, response);
		else if (request.getMethod().equals("TRACE"))
			return handlePOST(request, response);
		else
			return null;
	}
	
	public Response handleGET(Request request, Response response){
		return null;
	}
	
	public Response handlePOST(Request request, Response response){
		return null;
	}
	
	public Response handlePUT(Request request, Response response){
		return null;
	}
	
	public Response handleDELETE(Request request, Response response){
		return null;
	}
	
	public Response handleTRACE(Request request, Response response){
		return null;
	}
}
