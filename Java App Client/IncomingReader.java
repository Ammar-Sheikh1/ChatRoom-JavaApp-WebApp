/* CHAT ROOM <MyClass.java> 
 * * EE422C Project 7 submission by 
 * * Replace <...> with your actual data. 
 * * <Ammar Sheikh> * <as87934> 
 * * <76050> 
 * * Slip days used: <1> 
 * * Summer 2019 
 * */
package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class IncomingReader implements Runnable {
	private TextArea messageArea;
	private BufferedReader reader; 
	private MenuButton menuBtn;
	private Label chatLabel;
	private ArrayList<String> groupList;
	private ArrayList<String> userList;
	
	public IncomingReader(TextArea m,BufferedReader r,MenuButton b,Label c,ArrayList<String> g, ArrayList<String >u) {
		reader = r;
		menuBtn = b;
		messageArea = m;
		chatLabel = c;
		groupList = g;
		userList = u;
		
	}
	
	@Override
	public void run() {
		String message;
		System.out.println("New Thread init");
		try {
			while((message = reader.readLine()) != null) {
					JSONObject obj;
					obj = new JSONObject(message);
					String type = obj.getString("Type");
					System.out.println("Message received " + obj.toString());
					if(type.equals("message"))
						messageArea.appendText(obj.getString("Username") + " to " + obj.getString("Receiver") +  ": "+ obj.getString("Message") + "\n");
					else if(type.equals("userInfo")) {
						System.out.println("Receiving user info from server");
						JSONArray users = obj.getJSONArray("Username");
						
							menuBtn.getItems().clear();
							MenuItem b = new MenuItem("Broadcast");
							b.setOnAction(e->{chatLabel.setText(b.getText());});
							menuBtn.getItems().add(b);
							userList.clear();
							for(int i=0;i<users.length();i++) {
								MenuItem item = new MenuItem(users.getString(i));
								item.setOnAction(e->{chatLabel.setText(item.getText());
								messageArea.clear();
								});
								
								menuBtn.getItems().add(item);
								userList.add(users.getString(i));
							}
					}
							
					else if(type.equals("groupInfo")) {
						String group = obj.getString("Group");
						boolean inMenu = false;		
						for(MenuItem m : menuBtn.getItems()) {
							if(group.equals(m.getText())) { //Its in the menu, dont add
								inMenu = true;
								break;
							}
						}
						if(!inMenu) {
							groupList.add(group);
							MenuItem item = new MenuItem(group);
							item.setOnAction(e->{chatLabel.setText(item.getText());});
							menuBtn.getItems().add(item);
						}
					}
				} 
	
		} catch (IOException | JSONException e) {
			e.printStackTrace();
		}
		System.out.println("Leaving Runnable Thread");
	}

}
