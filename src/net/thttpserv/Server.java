package net.thttpserv;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

public class Server {
	public static final String VERSION = "v0.01";
	public static final String AUTHOR = "Jonathon Vogel";
	public static HashMap<String, String> ARGSET = new HashMap<String, String>(6);
	static {
		ARGSET.put("-ip     ", "ip to run server on");
		ARGSET.put("-port   ", "port to run server on - must be above 1000 or running as root on linux");
		ARGSET.put("-v      ", "0=nothing 1=verbose 2=debug");
		ARGSET.put("-ssl    ", "use SSL sockets");
		ARGSET.put("-threads", "max number of threads to pool");
		ARGSET.put("-help   ", "print this help message and quit");
	}
	
	static int verbose;
	static int maxThreads;
	static ExecutorService pool;
	static HandlerCreator factory;
	/**
	 * @param args
	 */
	public static void main(String[] args) {	
		HashMap<String, String> argm = parseArgs(args, ARGSET.keySet().toArray());
		if (argm.containsKey("-help") || argm.isEmpty()){
			Iterator<Entry<String, String>> iter = ARGSET.entrySet().iterator();
			while(iter.hasNext()){
				Entry<String, String> ent = iter.next();
				System.out.printf("%s : %s\n", ent.getKey(), ent.getValue());
			}
			System.exit(0);
		}
		try{
			verbose = Integer.parseInt(argm.get("-v"));
		} catch (Exception e){
			//oh god wat do
			System.err.println("Bad -v argument, defaulting to 1 (verbose). Try -help");
			verbose = 1;
		}
		boolean ssl = argm.containsKey("-ssl");
		String ip = argm.get("-ip");
		int port;
		
		if (ip == null)
			ip = "127.0.0.1";
		try {
			port = Integer.parseInt(argm.get("-port"));
		} catch (Exception e){
			log(1, "Bad/nonexisting port argument, defaulting to 80. Try -help");
			port = 80;
		}
		String s2 = argm.get("-threads");
		try {
			maxThreads = Integer.parseInt(s2);
		} catch (Exception e){
			log(1, "Defaulting to 10 threads...");
			maxThreads = 10;
		}
		
		pool = Executors.newFixedThreadPool(maxThreads);
		if (factory == null)
			setFactory(new HandlerCreator());
		log(1, "Starting server on %s:%s at %s %s\n", ip, port, getHTTPTimestamp(), (ssl ? " with ssl" : ""));
		log(1, "%d max threads\n", maxThreads);
		log(1, "Binding to port...");
		InetAddress bindTo = null;
		try{
			bindTo = InetAddress.getByName(ip);
		} catch (UnknownHostException e){
			System.err.println("Malformed ip " + ip);
			System.exit(1);
		}
		ServerSocket server = null;
		try{
			if (!ssl)
				server = ServerSocketFactory.getDefault().createServerSocket(port, 0, bindTo);
			else 
				server = SSLServerSocketFactory.getDefault().createServerSocket(port, 0, bindTo);
		} catch (IOException e){
			System.err.println("No connection");
			e.printStackTrace();
			System.exit(1);
		}
		log(1, "Waiting for connections...");
		
		try{
			while(true){
				//block for next connection
				Socket socket = server.accept();
				log(1, "Connection: %s at %s\n", socket.getInetAddress().toString(), getHTTPTimestamp());
				//handle in new thread, or queue if hitting max threads
				log(2, factory.getClass().getName());
				pool.execute(factory.createInstance(socket));
			}
		} catch (IOException e){
			System.err.println("No connection " + e);
			e.printStackTrace();
			System.exit(1);
		}
		try {
			server.close();
		} catch (IOException e) {}
	}	
	
	
	
	public static void readFile(Request req, Response resp) throws IOException {
		File f = new File(new File(".").getAbsolutePath() + req.path.getPath());
		System.out.println(f.getAbsolutePath());
		DataInputStream data = new DataInputStream(new FileInputStream(f));
		byte[] file = new byte[(int)f.length()];
		data.read(file);
		resp.setBody(file);
	}
	
	static private HashMap<String, String> parseArgs(String[] args, Object...flags){
		HashMap<String, String> ret = new HashMap<String, String>();
		final int NEW_ARG = 0;
		final int IN_ARG = 1;
		int state = NEW_ARG;
		String arg = "";
		for(String s : args){
			if (state == NEW_ARG){
				if (stringIn(s, flags)){
					arg = s;
					state = IN_ARG;
				}
			}
			else if (state == IN_ARG){
				if (!stringIn(s, flags)){
					ret.put(arg.trim(), s);
					state = NEW_ARG;
				}
				else{
					ret.put(arg.trim(), "");
					arg = s;
				}
			}
		}
		if (state == IN_ARG) //didn't clean up
			ret.put(arg.trim(), "");
		return ret;
	}
	
	static private boolean stringIn(String is, Object...in){
		for(Object s : in){
			if (s != null && s.toString().trim().equals(is))
				return true;
		}
		return false;
	}
	
	static SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd yyyy HH:mm:ss");	
	public static String getHTTPTimestamp(){
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		return formatter.format(cal.getTime()) + " GMT";
	}
	
	public static void log(int level, String log, Object...format){
		if (level >= 0 && level >= 2 - verbose) //not error
			System.out.printf(log + "\n", format);
		else if (level == -1)
			System.err.printf(log + "\n", format);
	}
	
	public static void setFactory(HandlerCreator newFactory){
		log(2, "Setting factory: %s", newFactory.getClass().getName());
		factory = newFactory;
	}

}