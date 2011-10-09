package net.thttpserv;

import java.net.Socket;

public class HandlerCreator {
	
	public ConnectionHandler createInstance(Socket s){
		return new ConnectionHandler(s);
	}
}
