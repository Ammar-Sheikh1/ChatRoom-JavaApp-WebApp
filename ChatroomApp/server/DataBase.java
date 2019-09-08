package server;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Database singleton class which retains all client information in a file.
 * @author Ammar
 *
 */
public class DataBase {
	
	//Maps username to client data
	private static DataBase db;
	private Map<String,ClientData> clientMap;
	private Map<String,JSONArray> groupMap;
	
	private Map<Pair,ArrayList<JSONObject>> chatHistory;
	
	private File clientDataFile = null;
	
	
	private DataBase() {}
	
	public static DataBase createDB(File f){
		db = new DataBase();
		db.clientMap = new ConcurrentHashMap<String,ClientData>();
		db.groupMap = new ConcurrentHashMap<String,JSONArray>();
		db.chatHistory = new ConcurrentHashMap<Pair,ArrayList<JSONObject>>();
		db.clientDataFile = f;
		db.updateDB();
		return db;
	}
	
	public static DataBase instance(){
		if(db == null)
			return null;
		return db;
	}
	
	public void saveDB(){
		try {
			ObjectOutputStream objectStream = new ObjectOutputStream(new FileOutputStream(clientDataFile));
			objectStream.writeObject(clientMap);
			objectStream.close();
		} catch(IOException e) {
			e.printStackTrace();
		} 
	}
	
	
	@SuppressWarnings("unchecked")
	public void updateDB() {
		
		try {
			ObjectInputStream objectStream = new ObjectInputStream(new FileInputStream(clientDataFile));
			clientMap = (ConcurrentHashMap<String,ClientData>)objectStream.readObject();
			objectStream.close();
		} catch(EOFException ignore){
			//Ignored, since File may be empty, and that is ok
		}
		catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
			
	}
		
	public boolean addClientInfo(String username,String password,AsynchronousSocketChannel s,ClientHandler c) {
		if(clientMap.containsKey(username)) {
			return false;
		}
		clientMap.put(username, new ClientData(username, password,s,true,c));
		db.saveDB(); 
		return true;
	}
	
	public boolean containsUser(String username) {
		return clientMap.containsKey(username);
	}
	
	public boolean verifyPassword(String username, String password) {
	if(!clientMap.containsKey(username))
		return false;
		
	return clientMap.get(username).getPassword().equals(password);
	}
	
	public AsynchronousSocketChannel getUserSocket(String username) {
		if(!clientMap.containsKey(username))
			return null;
		return clientMap.get(username).getSocket();
	}
	
	public ClientData getClientData(String username) {
		if(!clientMap.containsKey(username))
			return null;
		return clientMap.get(username);
	}
	
	public Map<String,ClientData> getClientMap() {
		return clientMap;
	}
	
	public boolean addGroup(String groupName,JSONArray arr) {
		if(groupMap.containsKey(groupName)) {
			return false;
		}
		groupMap.put(groupName, arr);
		return true;
	}
	
	public Map<String,JSONArray> getGroupMap( ) {return groupMap;}
	
	public boolean addToGroup(String group,String user) {
		if(!groupMap.containsKey(group))
			return false;
		JSONArray arr = groupMap.get(group);
		arr.put(user);
		return true;
	}

	public Map<Pair, ArrayList<JSONObject>> getChatHistory() {
		return chatHistory;
	}

	public void setChatHistory(Map<Pair, ArrayList<JSONObject>> chatHistory) {
		this.chatHistory = chatHistory;
	}
	
	public void addMessageToHistory(String user,String receiver, JSONObject obj) {
		if(chatHistory.containsKey(new Pair(user,receiver))) {
			chatHistory.get(new Pair(user,receiver)).add(obj);
		}
		else {
			chatHistory.put(new Pair(user,receiver), new ArrayList<JSONObject>());
			chatHistory.get(new Pair(user,receiver)).add(obj);
		}
		return;
	}
	
	public ArrayList<JSONObject> getchatHistoryOf(String user1,String user2) {
		if(chatHistory.containsKey(new Pair(user1,user2))) {
			return chatHistory.get(new Pair(user1,user2));
		}
		return null;
	}
	
}
