package server;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * AppHandler class handles all interactions with a Java App client
 * @author Ammar
 *
 */

public class AppHandler extends ClientHandler implements Runnable{
	
	
	/**
	 * AsynchronousSocketChannel client;
		String username;
		ClientData clientData;
	 */
	
	//Data buffer for JSON strings
	private ByteBuffer buffer = ByteBuffer.allocate(128);
	
	Thread hold;

	public AppHandler(AsynchronousSocketChannel s) {
		super(s);
	}

	@Override
	public void run() {
		System.out.println("In AppHandlers Run by Thread " + Thread.currentThread());
		client.read(buffer, null, new CompletionHandler<Integer,ByteBuffer>(){
			@Override
			public void completed(Integer result, ByteBuffer attachment) {
				System.out.println("In AppHandlers CompletedHandler by Thread " + Thread.currentThread());
				printBuffer();
				if(state == State.LOGIN) {
					try {
						loginUser();
					} catch (UnsupportedEncodingException | JSONException | InterruptedException |ExecutionException  e) {
						e.printStackTrace();
					}
				}
				else if(state == State.MESSAGE) {
					
				}
			}

			@Override
			public void failed(Throwable exc, ByteBuffer attachment) {
				System.out.println("Error!!!");
			}
			
		});		
		System.out.println("Leaving AppHandler");
	}
	
	/**
	 * Create JSONObject, responsd to user
	 * @throws UnsupportedEncodingException 
	 * @throws JSONException 
	 * @throws ExecutionException 
	 * @throws InterruptedException 
	 */

	public void loginUser() throws UnsupportedEncodingException, JSONException, InterruptedException, ExecutionException {
		String message = new String(buffer.array(), "UTF-8");
		JSONObject obj = new JSONObject(message);
		String user = obj.getString("Username");
		String type = obj.getString("Type");
		
		if(type.equals("createUser")) {
			String p = obj.getString("Password");
			if(DataBase.instance().containsUser(user)) {
				obj.put("Status", "false");
				writeStringAsBuffer(obj.toString());
			}
			else { 	//Add user to DataBase and return
				//DataBase.instance().addClientInfo(u, p, clientSocket,this);
				clientData = DataBase.instance().getClientData(user);
				username = user;
				obj.put("Status", "true");
				//updateClients();
				writeStringAsBuffer(obj.toString());
				return;
			}
		}

		else if(type.equals("verify")) {
			String pass = obj.getString("Password");
			if(obj.getString("Type").equals("verify") && DataBase.instance().verifyPassword(user,pass)) {
				obj.put("Status", "true");
				//DataBase.instance().getClientData(user).SetSocket(clientSocket);
				DataBase.instance().getClientData(user).setOnline(true);
				//DataBase.instance().getClientData(u).setClientHandler(this);
				username = user;
				writeStringAsBuffer(obj.toString());
				//updateClients();
				return;
			}
			else {
				obj.put("Status","false");
				writeStringAsBuffer(obj.toString());
			}
		}

		else if(type.equals("exit")) {
			//TODO: Need to update DB 
		}
	}

	private void printBuffer() {
		System.out.println("Printing Buffer:");
		byte[] t = buffer.array();
		for(int i=0;i<t.length;i++) {
			System.out.print((char)t[i]);
		}
		System.out.println();
	}
	
	private int writeStringAsBuffer(String s) throws InterruptedException, ExecutionException {
		ByteBuffer b = ByteBuffer.wrap(s.getBytes());
		Future<Integer> future = client.write(b);
		return future.get();
	}
	
	
	
}
