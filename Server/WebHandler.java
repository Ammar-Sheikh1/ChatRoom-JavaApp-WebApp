package project7;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * TODO: UNFINISHED
 * Web Handler class assigned to every web client
 * @author Ammar
 *
 */
public class WebHandler implements Runnable {
	
	Socket clientSocket;
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
		//loginUser
		//updateClients
		//clientMessageMode()
	}
	
	/**
	 * @throws IOException 
	 * @throws JSONException 
	 * 
	 */
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
				String user = obj.getString("User");
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
		
	}
	
	
	

}
