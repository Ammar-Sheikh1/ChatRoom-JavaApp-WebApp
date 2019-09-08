package server;

import java.nio.channels.AsynchronousSocketChannel;
/**
 * 
 * @author Ammar
 *
 */
public class ClientHandler{
	
	
	//Socket & user info
	protected AsynchronousSocketChannel clientSocket;
	protected String username;
	protected ClientData clientData;
	
	enum State{
		LOGIN,
		MESSAGE,
	}
	State state;
	
	public ClientHandler(AsynchronousSocketChannel s) {
		clientSocket = s;
		state = State.LOGIN;
	}

	
}