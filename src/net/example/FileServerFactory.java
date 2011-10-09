package net.example;

import java.net.Socket;

import net.thttpserv.ConnectionHandler;
import net.thttpserv.HandlerCreator;

public class FileServerFactory extends HandlerCreator{
	public ConnectionHandler createInstance(Socket s){
		return new FileServer(s);
	}
}
