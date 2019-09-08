package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TODO: UNFINISHED, need to add support for multiple client protocol upgrading
 * (HTTP -> WS ).Also need to remove tester code
 * 		
 * @author Ammar
 *
 */

public class WebSocketServerMain implements Runnable{

	@Override
	public void run(){
	/*	ServerSocket server = null;
		try {
			server = new ServerSocket(8080);
			System.out.println("Web server up at port 8080");
			
			while(true) {
				//Accept client, upgrade request and start new thread
				Socket client = server.accept();
				Thread t = new Thread(new WebHandler(client));
				t.start();
			}
			
			
		} catch(IOException e) {
			e.printStackTrace();
		}*/
		
		
		//server.close
	}
}
