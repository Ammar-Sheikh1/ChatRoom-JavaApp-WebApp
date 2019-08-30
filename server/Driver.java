package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Driver{

	static int clientCount = 0;
	static boolean running = true;
	
	public static void main(String[] args) throws IOException {
		
		
		ServerSocket serverSocket = null;;
		DataBase clientDB = DataBase.createDB(new File("ClientData.ser"));
		
		//Web App Server socket will be run on a different thread
		Thread webServer = new Thread(new WebSocketServerMain());
		
		try {
			
			webServer.start();
			serverSocket = new ServerSocket(4040);
		
			System.out.println("App Server Socket is bound at Port " + serverSocket.getLocalPort());			
			
			while(true) {
				Socket clientSideSock = serverSocket.accept();
				System.out.println("Connection made: " + ++clientCount);
				Thread t = new Thread(new ClientHandler(clientSideSock));
				t.start();
			}
		}
		catch (IOException e1) {
			System.out.println("Java App Server/Connection Failed");
			e1.printStackTrace();
		}
		
		serverSocket.close();
		clientDB.saveDB();
	}


}
