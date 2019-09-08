package server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO: UNFINISHED
 * Web Handler class assigned to every web client
 * @author Ammar
 *
 */
public class WebHandler implements Runnable {

	@Override
	public void run() {}
	
	/*Socket clientSocket;
	String username;
	
	InputStream reader;
	OutputStream writer;
	
	ClientData client;
	
	byte[] messageBuffer; 
		
	public WebHandler(Socket s) {
		clientSocket = s;
		messageBuffer = new byte[128];
		username = null;
		client = null;
		try {
			reader = s.getInputStream();
			writer = s.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}


	@Override
	public void run() {
		
		//Upgrade Request
		Scanner s = new Scanner(reader, "UTF-8");
		String data = s.useDelimiter("\\r\\n\\r\\n").next();
		Matcher get = Pattern.compile("^GET").matcher(data);
		if(get.find()) {
		Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
		match.find();
			try {
				byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
						+ "Connection: Upgrade\r\n"
						+ "Upgrade: websocket\r\n"
						+ "Sec-WebSocket-Accept: "
						+ Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
						+ "\r\n\r\n").getBytes("UTF-8");
				writer.write(response, 0, response.length);	
			}catch (NoSuchAlgorithmException | IOException e) {
				e.printStackTrace();
			}
		}	
		System.out.println("Connected & Upgraded user at port " + clientSocket.getPort());
		try {
			loginUser();
		} catch(JSONException | IOException e) {
			e.printStackTrace();
		}

		//updateClients
		//clientMessageMode()
	}
	
	/**
	 * @throws IOException 
	 * @throws JSONException 
	 * 
	 */
	
	/*
	private void loginUser() throws IOException, JSONException {
		while(true) {
			int readCount = reader.read(messageBuffer);
			String message = WebSocketDecoder.decodeClient(messageBuffer, readCount);
			JSONObject obj = new JSONObject(message);
			if(obj.getString("Type").equals("createUser")) {
				//Check if the user already exists, then create/notify 
			}
			else if(obj.getString("Type").equals("verify")) {
				//verify the password/user is correct, then login/notify
				String user = obj.getString("Username");
				String pass = obj.getString("Password");
				if(DataBase.instance().verifyPassword(user, pass)) {
					DataBase.instance().getClientData(user).SetSocket(clientSocket);
					DataBase.instance().getClientData(user).setOnline(true);
					//DataBase.instance().getClientData(user).setClientHandler(this);
					username = user;
					obj.put("Verify", "true");
					writer.write(WebSocketEncoder.encodeString(obj.toString()));
					//writer.flush?
				}
				//User/Pass incorecct notify client
				else {
					obj.put("Verify", "false");
					//Should the encodestring really be creating a new object?
					//or should i pass in the buffer that i want to write to?
					writer.write(WebSocketEncoder.encodeString(obj.toString()));
					//writer.flush?
					
				}
			}
			else if(obj.get("Type").equals("exit")) {
				
			}
			
		}
		
	}*/
	
	
	

}
