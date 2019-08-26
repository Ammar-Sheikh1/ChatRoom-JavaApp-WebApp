package project7;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Driver{

	static int clientCount = 0;
	static boolean running = true;
	
	public static void main(String[] args) throws IOException {
		
		ServerSocket serverSocket;
				
		DataBase clientDB = DataBase.createDB(new File("ClientData.ser"));
		
		Thread webServer = new Thread(new WebSocketServerMain());
		
		try {
			
			//Create Java App server on port 1024
			webServer.start();
			serverSocket = new ServerSocket(4040);
		
			System.out.println("App Server Socket is bound at Port " + serverSocket.getLocalPort());
			System.out.println("Number of Threads running: " + Thread.activeCount());
			
			while(true) {
				@SuppressWarnings("unused")
				Socket clientSideSock = serverSocket.accept();
				System.out.println("Connection made: " + ++clientCount);
				Thread t = new Thread(new ClientHandler(clientSideSock));
				t.start();
			}
					
		}
		catch (IOException e1) {
			System.out.println("One of the servers failed to create");
			e1.printStackTrace();
		}
		
		
		//serverSocket.close();
		//server.stop(0);
		System.out.println("Done");
		
		clientDB.saveDB();
	}


}
