package project7;

import java.io.Serializable;
import java.net.Socket;

public class ClientData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username;
	private String password;
	private transient Socket clientSocket;
	private transient boolean isOnline;
	private transient ClientHandler clientHandler;
	//Need to store visilbity!!!!
	
	public ClientData(String name, String pass) {
		username = name;
		password = pass;
		isOnline = true;
		clientSocket = null;
	}
	
	public ClientData(String name,String pass, Socket s) {
		this(name,pass);
		clientSocket = s;
	}
	
	public ClientData(String n,String p,Socket s,boolean b,ClientHandler c) {
		this(n,p,s);
		isOnline = b;
		clientHandler = c;
	}

	
	public String getUsername() {return username;}
	
	public void setPassword(String pass) {password = pass;}
	public String getPassword() {return password;}
	
	public void SetSocket(Socket s) { clientSocket = s;}
	
	public Socket getSocket() {return clientSocket;}
	
	public boolean isOnline() {return isOnline;}
	
	public void setOnline(boolean b) { isOnline = b;}

	public ClientHandler getClientHandler() {
		return clientHandler;
	}

	public void setClientHandler(ClientHandler clientHandler) {
		this.clientHandler = clientHandler;
	}

}