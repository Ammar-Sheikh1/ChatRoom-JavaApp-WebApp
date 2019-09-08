package server;

import java.net.Socket;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
/**
 * 
 * @author Ammar
 *
 */
public class ClientHandler{
	
	
	//Socket & user info
	protected AsynchronousSocketChannel client;
	protected String username;
	protected ClientData clientData;
	
	enum State{
		LOGIN,
		MESSAGE,
	}
	State state;
	
	public ClientHandler(AsynchronousSocketChannel s) {
		client = s;
		state = State.LOGIN;
	}

	
}