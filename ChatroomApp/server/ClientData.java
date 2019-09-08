package server;

import java.io.Serializable;
import java.net.Socket;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousSocketChannel;


/**
 * Client Data class used to store information about each client
 * 
 * @author Ammar
 *
 */
public class ClientData implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private transient AsynchronousSocketChannel clientSocket;
	private transient boolean isOnline;
	private transient ClientHandler clientHandler;
	
	public ClientData(String name, String pass) {
		username = name;
		password = pass;
		isOnline = true;
		clientSocket = null;
	}
	
	public ClientData(String name,String pass, AsynchronousSocketChannel s) {
		this(name,pass);
		clientSocket = s;
	}
	
	public ClientData(String n,String p,AsynchronousSocketChannel s,boolean b,ClientHandler c) {
		this(n,p,s);
		isOnline = b;
		clientHandler = c;
	}

	
	public String getUsername() {return username;}
	
	public void setPassword(String pass) {password = pass;}
	public String getPassword() {return password;}
	
	public void SetSocket(AsynchronousSocketChannel s) { clientSocket = s;}
	
	public AsynchronousSocketChannel getSocket() {return clientSocket;}
	
	public boolean isOnline() {return isOnline;}
	
	public void setOnline(boolean b) { isOnline = b;}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}

	public void setClientHandler(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}

}
