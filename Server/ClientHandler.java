package project7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * ClientHandler class handles all interactions with a Java App client
 * @author Ammar
 *
 */

public class ClientHandler implements Runnable{
	
	//The socket which we are using to connect with our clients
	Socket clientSocket;
	String username;
	
	//Either initialized or fetched from DB
	ClientData client;
	boolean isVerified = false;
	
	//The stream which will be used to read and write data socket connections
	BufferedReader reader;
	PrintWriter writer;
	
	public ClientHandler(Socket s) {
		
		try {
			clientSocket = s;
			reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			writer = new PrintWriter(clientSocket.getOutputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	
		/**
		 * A client handler's object is to receive incomrming messages then route them to the recieving socket
		 * 
		 */
		@Override
		public void run() {
			try {
				loginUser(); 
				updateClients();
				clientMessageMode();
			} catch (IOException | JSONException e) {
				e.printStackTrace();
			}
	
		}

		/*
		 * Handles pre-existing accounts and accounts that are being newly created
		 *  If invalid login or invalid new account, it notifies client
		 * @throws JSONException
		 * @throws IOException
		 */
		private void loginUser() throws JSONException, IOException{
			String message = null;

			while((message = reader.readLine()) != null){
				System.out.println("Message " + message);
				JSONObject obj = new JSONObject(message);
				System.out.println("Message recieved " + obj.toString());
				String u = obj.getString("Username");
				String t = obj.getString("Type");

				if(t.equals("createUser")) {
					String p = obj.getString("Password");
					if(DataBase.instance().containsUser(u)) {
						obj.put("Status", "false");
						writer.println(obj.toString());
						writer.flush();
					}
					else {
						//Add user to DataBase and return
						DataBase.instance().addClientInfo(u, p, clientSocket,this);
						client = DataBase.instance().getClientData(u);
						username = u;
						obj.put("Status", "true");
						updateClients();
						writer.println(obj.toString());
						writer.flush();
						return;
					}
				}


				if(t.equals("verify")) {
					String p = obj.getString("Password");
					if(obj.getString("Type").equals("verify") && DataBase.instance().verifyPassword(u,p)) {
						obj.put("Status", "true");
						DataBase.instance().getClientData(u).SetSocket(clientSocket);
						DataBase.instance().getClientData(u).setOnline(true);
						DataBase.instance().getClientData(u).setClientHandler(this);
						username = u;
						writer.println(obj.toString());
						writer.flush();
						updateClients();
						return;
					}
					else {
						obj.put("Status","false");
						writer.println(obj.toString());
						writer.flush();
					}
				}

				if(t.equals("exit")) {
					//TODO: Need to update DB 
				}
			}
		}

		/**
		 * Main Chat method
		 * This method just redirects Messages to Receiver Clients
		 * @throws IOException
		 * @throws JSONException
		 */
		private void clientMessageMode() throws IOException, JSONException{
			String message = null;
			System.out.println("In the clientMessageMode function");
			while((message = reader.readLine()) != null) {
				System.out.println("Message " + message);
				JSONObject obj = new JSONObject(message);
				String user = obj.getString("Username");
				String type = obj.getString("Type");

				if(type.equals("message")) {
					DataBase.instance().addMessageToHistory(user, obj.getString("Receiver"), obj); //Add the message to chatHist.

					if(DataBase.instance().getGroupMap().containsKey(obj.getString("Receiver"))) {
						String groupName = obj.getString("Receiver");
						System.out.println("GroupChat Messge Received");
						JSONArray arr = DataBase.instance().getGroupMap().get(groupName);
						for(int i=0;i<arr.length();i++) {
							if(arr.getString(i).equals(user) == false) { //if the sender is not 
								ClientData c = DataBase.instance().getClientData(arr.getString(i));
								c.getClientHandler().writer.println(obj.toString());
								c.getClientHandler().writer.flush();
							}
						}
					}
					else {
						String rec = obj.getString("Receiver");
						//For Broadcast, we send to all sockets
						if(rec.equals("Broadcast")) {
							for(Map.Entry<String, ClientData> client : DataBase.instance().getClientMap().entrySet()) {
								if(client.getValue().getClientHandler() != null && !user.equals(client.getKey())) {
									client.getValue().getClientHandler().writer.println(obj.toString());
									client.getValue().getClientHandler().writer.flush();
								}
							}

						}

						//This is regular messages//
						Socket recSocket = DataBase.instance().getUserSocket(rec);
						//TODO: CHANGE THIS CRAP CODE!!!
						if(recSocket != null) {
							System.out.println("message passed on to " + rec);
							PrintWriter write = new PrintWriter(recSocket.getOutputStream());
							write.println(obj.toString());
							write.flush();
						}
					}
				}

				else if(type.equals("history")) {
					String other_user = obj.getString("Receiver");
					ArrayList<JSONObject> history = DataBase.instance().getchatHistoryOf(user, other_user);
					if(history != null) {
						for(JSONObject o : history) {
							writer.println(o.toString());
							writer.flush();
						}
					}
				}


				//Add to DataBase and tell all added members that they were added to the group
				else if(type.equals("createGroup")) {
					DataBase.instance().addGroup(obj.getString("Name"), obj.getJSONArray("Group"));
					updateClients(); //update all clients of new Groups
				}
				else if(type.equals("groupAddition")) {
					String group = obj.getString("Group");
					String userToAdd = obj.getString("User");
					DataBase.instance().addToGroup(group, userToAdd);
					updateClients();
				}

				else if(type.equals("exit")) {
					DataBase.instance().getClientData(user).setOnline(false);
					updateClients();
				}

			}
		}
		
		
		/**
		 * -This method will be called everytime a new group is created and everytime
		 * a new user leaves or exits
		 * -Notifes client of new information, such as new users and new Groups that they 
		 * are in
		 * 
		 * -This will update All clients when one opens up or closes the application
		 * @return
		 * @throws JSONException
		 */
		private boolean updateClients() throws JSONException{
			System.out.println("In the Update Client function");
			JSONObject obj = new JSONObject();
			obj.put("Type", "userInfo");
			JSONArray arr = new JSONArray(); //This is an array of online usernames
			ArrayList<ClientData> c = new ArrayList<ClientData>();
			
			//Every 
			for(Map.Entry<String, ClientData> entry : DataBase.instance().getClientMap().entrySet()) {
				if(entry.getValue().isOnline()) {
					arr.put(entry.getValue().getUsername());
					c.add(entry.getValue());
				}
			}
			
			obj.put("Username",arr);
			System.out.println("Updating all clients with " + obj.toString());

			//Tell all clients that are online, about the new client
			for(ClientData client : c) {
				client.getClientHandler().writer.println(obj.toString());
				client.getClientHandler().writer.flush();
			}
			
			//Go thru every group
			obj.remove("Username");
			
			//Now we send groups
			obj.put("Type", "groupInfo");
			String groupName;
			
			for(Map.Entry<String, JSONArray> entry : DataBase.instance().getGroupMap().entrySet()) {
				JSONArray array = entry.getValue();
				groupName = entry.getKey(); //Put that Groups name in the info array
				obj.put("Group", groupName);
				//Tell everyone in the group about the group info
				for(int i=0;i<array.length();i++) {
					String member = array.getString(i);
					ClientData client = DataBase.instance().getClientData(member);
					client.getClientHandler().writer.println(obj.toString());
					client.getClientHandler().writer.flush();
				}
			}
		
			return true;
		}
	
}
