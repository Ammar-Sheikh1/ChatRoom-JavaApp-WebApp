package client;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Display extends Application{
	
	Stage window;
	
	final int windowLength = 900;
	final int windowHeight = 500;
	private Socket chatSocket;
	private String clientUsername = "";
	private BufferedReader reader;
	private PrintWriter writer;
	private ArrayList<String> groupList = new ArrayList<String>();
	private ArrayList<String> userList = new ArrayList<String>();
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		window = primaryStage;
		window.setTitle("UT Chat");
		
		//Create Login Scene
		BorderPane loginPane = new BorderPane();
		loginPane.setPadding(new Insets(20,20,20,20));
		loginPane.setStyle("-fx-background-color: indianred");
		
		Image logo = new Image("file:src/assignment7/longhorn.png");
		ImageView logoView = new ImageView(logo);
		
		VBox loginBox = new VBox();
		loginBox.setAlignment(Pos.BOTTOM_CENTER);
		loginBox.setSpacing(5);
		TextField usernameTextField = new TextField();
		usernameTextField.setPromptText("Username");
		usernameTextField.setStyle("-fx-alignment: center");
		usernameTextField.setMaxSize(150, 20);
		
		TextField passwordTextField = new TextField();
		passwordTextField.setPromptText("Password");
		passwordTextField.setStyle("-fx-alignment: center");
		passwordTextField.setMaxSize(150,20);
		
		Button loginButton = new Button("Login");
		loginButton.setStyle("-fx-text-alignment: center");
		loginButton.setStyle("-fx-alignment: center");
		loginButton.setMinWidth(100);
		
		Button newAccountBtn = new Button("Create Account");
		newAccountBtn.setStyle("-fx-text-alignment: center");
		newAccountBtn.setStyle("-fx-alignment: center");
		newAccountBtn.setMinWidth(100);
		
		loginBox.getChildren().addAll(logoView,usernameTextField,passwordTextField,loginButton,newAccountBtn);
		loginPane.setCenter(loginBox);
		
		Scene loginScene = new Scene(loginPane, windowLength,windowHeight);
		
			//ipAddress scene
		
				BorderPane p = new BorderPane();
				p.setStyle("-fx-background-color: indianred");
				
				TextField ipAddress = new TextField();
				ipAddress.setMaxWidth(200);
				ipAddress.setPromptText("IP Address");
				ipAddress.setStyle("-fx-alignment: center");
				
				Button bb = new Button("Connect");
				bb.setOnAction(e->{
					try {
						chatSocket = new Socket(ipAddress.getText(),4040);
						writer = new PrintWriter(chatSocket.getOutputStream());
						reader = new BufferedReader(new InputStreamReader(chatSocket.getInputStream()));
						window.setScene(loginScene);
						
					} catch(Exception ee) {
						ee.printStackTrace();
					}
						
				});
				bb.setStyle("-fx-text-alignment: center");
				bb.setAlignment(Pos.CENTER);
				VBox boxx = new VBox(ipAddress,bb);
				boxx.setAlignment(Pos.CENTER);
				
				p.setCenter(boxx);
				//p.setAlignment(boxx, Pos.CENTER);

				Scene ss = new Scene(p,windowLength,windowHeight);
				window.setScene(ss);
				window.show();
				
		
		//
		//
		// MainChat Scene
		//
		//
		BorderPane chatPane = new BorderPane();
		
		TextArea messageArea = new TextArea("Welcome to UT Chat today is " + LocalDateTime.now() + "\n");
		messageArea.setEditable(false);
		
		HBox bottomBox = new HBox();
		bottomBox.setMinHeight(50);
		bottomBox.setAlignment(Pos.CENTER);
		bottomBox.setSpacing(50);
		
		TextField messageInputArea = new TextField();
		messageInputArea.setMinWidth(500);
		
		Button sendButton = new Button("Send");
		bottomBox.getChildren().addAll(messageInputArea,sendButton);
		
		VBox leftBox = new VBox();
		leftBox.setSpacing(30);
		
		MenuButton visibilityButton = new MenuButton("Visibility");
		MenuItem visibleToAll = new MenuItem("All");
		MenuItem visibleToFriends = new MenuItem("Friends");
		MenuItem visibleToNone = new MenuItem("None");
		visibilityButton.getItems().addAll(visibleToAll,visibleToFriends,visibleToNone);
		
		Button createGroupBtn = new Button("Create Group");
		
		Button addToGroupBtn = new Button("Add To Group");
		
		Button historyBtn = new Button("History");
		
		leftBox.getChildren().setAll(visibilityButton,createGroupBtn,addToGroupBtn,historyBtn);
		
		
		VBox rightBox = new VBox();
		MenuButton activeUsers = new MenuButton("Users");
		MenuItem broadcastItem = new MenuItem("~");
		activeUsers.getItems().addAll(broadcastItem);
		rightBox.getChildren().setAll(activeUsers);
		
		
		HBox topBox = new HBox();
		topBox.setMinHeight(50);
		topBox.setAlignment(Pos.CENTER);
		Label receiverLabel = new Label();
		receiverLabel.setAlignment(Pos.CENTER);
		receiverLabel.setText("Broadcast");
		
		topBox.getChildren().add(receiverLabel);
		
		chatPane.setCenter(messageArea);
		chatPane.setTop(topBox);
		chatPane.setBottom(bottomBox);
		chatPane.setLeft(leftBox);
		chatPane.setRight(rightBox);
		
		Scene chatScene = new Scene(chatPane,windowLength,windowHeight);
			
		
		window.setOnCloseRequest(e->{
			if(chatSocket == null) {
				System.exit(0);
			}
			System.out.println("Window Closing");
			JSONObject obj = new JSONObject();
			try {
				obj.put("Type", "exit");
				obj.put("Username", clientUsername);
				System.out.println("Sent this: " + obj.toString());
				writer.println(obj.toString());
				writer.flush();
				} catch (JSONException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
			
		});
		
		
		sendButton.setOnAction(e->{
			JSONObject obj = new JSONObject();
			try {
				obj.put("Type", "message");
				obj.put("Username", clientUsername);
				obj.put("Message", messageInputArea.getCharacters());
				obj.put("Receiver", receiverLabel.getText());
				System.out.println("Sent this: " + obj.toString());
				writer.println(obj.toString());
				writer.flush();
			} catch (JSONException e1) {
				System.out.println("JSON ERROR ON SEND BUTTON");
				e1.printStackTrace();
			}
			messageArea.appendText(clientUsername + " to " + receiverLabel.getText() + ": "+messageInputArea.getCharacters() +"\n");
			messageInputArea.clear();
			messageInputArea.requestFocus();
		});
		
		messageInputArea.setOnKeyPressed(e->{
			if (e.getCode() == KeyCode.ENTER) {
				JSONObject obj = new JSONObject();
				try {
					obj.put("Type", "message");
					obj.put("Username", clientUsername);
					obj.put("Message", messageInputArea.getCharacters());
					obj.put("Receiver", receiverLabel.getText());
					System.out.println("Sent this: " + obj.toString());
					writer.println(obj.toString());
					writer.flush();
				} catch (JSONException e1) {
					System.out.println("JSON ERROR ON SEND BUTTON");
					e1.printStackTrace();
				}
				messageArea.appendText(clientUsername +": "+messageInputArea.getCharacters() +"\n");
				messageInputArea.clear();
				messageInputArea.requestFocus();
			}
		});
		
		
		newAccountBtn.setOnAction(e->{
			JSONObject obj = new JSONObject();
			try {
				obj.put("Type", "createUser");
				obj.put("Username", usernameTextField.getCharacters().toString());
				obj.put("Password", passwordTextField.getCharacters().toString());
				writer.println(obj.toString());
				writer.flush();
				try {
					obj = new JSONObject(getResponse());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(obj.getString("Status").equals("true")) {
					clientUsername = obj.getString("Username");
					messageArea.appendText("Welcome " + clientUsername + "\n");
					window.setScene(chatScene);
					Thread t = new Thread(new IncomingReader(messageArea,reader,activeUsers,receiverLabel,groupList,userList));
					t.start();
				}
				else {
					System.out.println("Wrong password");
				}
				
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		});
		
		
		loginButton.setOnAction(e-> {
		
			try {
				JSONObject obj = new JSONObject();
				obj.put("Type", "verify");
				obj.put("Username", usernameTextField.getCharacters().toString());
				obj.put("Password", passwordTextField.getCharacters().toString());
				writer.println(obj.toString());
				writer.flush();
				try {
					obj = new JSONObject(getResponse());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(obj.getString("Status").equals("true")) {
					clientUsername = obj.getString("Username");
					messageArea.appendText("Welcome " + clientUsername + "\n");
					window.setScene(chatScene);
					Thread t = new Thread(new IncomingReader(messageArea,reader,activeUsers,receiverLabel,groupList,userList));
					t.start();
				}
				else {
					System.out.println("Wrong password");
				}
				System.out.println("sent this " + obj.toString());
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		});
		
		historyBtn.setOnAction(e->{
			JSONObject obj = new JSONObject();
			try {
			obj.put("Type", "history");
			obj.put("Username", clientUsername);
			obj.put("Receiver", receiverLabel.getText());
			} catch(JSONException e1) {
				e1.printStackTrace();
			}
			writer.println(obj.toString());
			writer.flush();
		});
		
		
		addToGroupBtn.setOnAction(e->{
			Stage stage = new Stage();
			VBox box = new VBox();
			box.setAlignment(Pos.CENTER);
			MenuButton groups = new MenuButton("Groups");
			MenuButton users = new MenuButton("Users");
			Label selectedGroup = new Label();
			Label selectedUser = new Label();
			Button b = new Button("Add To Group");
			
			for(String group : groupList) {
				MenuItem item = new MenuItem(group);
				item.setOnAction(e1->{
					selectedGroup.setText(item.getText());
				});
				groups.getItems().add(item);
			}
			
			for(String user : userList) {
				MenuItem item = new MenuItem(user);
				item.setOnAction(e1->{
					selectedUser.setText(item.getText());
				});
				users.getItems().add(item);
			}
			
			b.setOnAction(e3->{
				if(selectedGroup.getText().equals("")) {
					selectedGroup.setText("Need to Select a Group");
				}
				if(selectedUser.getText().equals("")) {
					selectedUser.setText("Need to Select a User");
				}
				else if(selectedUser.getText().length() > 0 && selectedGroup.getText().length() >0) {
					JSONObject obj = new JSONObject();
					try {
					obj.put("Type", "groupAddition");
					obj.put("Username", clientUsername);
					obj.put("Group", selectedGroup.getText());
					obj.put("User", selectedUser.getText());
					writer.println(obj.toString());
					writer.flush();
					}
					catch(JSONException e5) {
						e5.printStackTrace();
					}
				}
			});
			box.getChildren().addAll(groups,users,selectedGroup,selectedUser,b);
			Scene s = new Scene(box,300,200);
			stage.setScene(s);
			stage.show();
			
		});
		
		
		//This button is used to create a new group.
		// A new window will show us and give you the options of people to select
		createGroupBtn.setOnAction(e->{
			JSONObject obj = new JSONObject();
			try {
				obj.put("Type", "createGroup");
				obj.put("Username", clientUsername);
			} catch (JSONException e4) {
				e4.printStackTrace();
			}
			JSONArray arr = new JSONArray();
			Stage group = new Stage();
			VBox box = new VBox();
			box.setAlignment(Pos.CENTER);
			TextField groupName= new TextField();
			groupName.setPromptText("Create Name for Group");
			Button b = new Button("Create Group");
			MenuButton users = new MenuButton();
			Label label = new Label();
			
			for(MenuItem i : activeUsers.getItems()) {
				MenuItem l = new MenuItem(i.getText());
				l.setOnAction(e1->{
					label.setText(l.getText() + " has been added to the Group");
					arr.put(l.getText());
				});
				users.getItems().add(l);
			}
			
			//TODO: Allow Support for same group names!
			b.setOnAction(e2->{
				if(arr.length() <= 1) { 
					label.setText("Groups need more than 1 person");
				}
				else if(groupName.getText().equals("")) {
					label.setText("Must add a group name");
				}
				else {
					arr.put(clientUsername);
					try {
						obj.put("Group", arr);
						obj.put("Name", groupName.getText());
						writer.println(obj.toString());
						writer.flush();
					}
					catch(JSONException e5){
						e5.printStackTrace();
					}
					//Now I need to update the MenuItem on the main screen
					//MenuItem k = new MenuItem(groupName.getText());
					//k.setOnAction(e3->{receiverLabel.setText(k.getText());});
					//activeUsers.getItems().add(k);
					group.close();
				}
			});
			
			box.getChildren().addAll(groupName,b,label,users);
			Scene scene = new Scene(box,300,150);
			group.setScene(scene);
			group.show();
		});
		
		
		
		
	}
	
	private String getResponse() throws IOException{
		char[] message = new char[1024];
		InputStreamReader r = new InputStreamReader(chatSocket.getInputStream());
		r.read(message);
		String s = new String(message);
		System.out.println("Received " + s);
		return s;
	}

	public static void LaunchApp (String[] args) {
		launch(args);
	}
	

}
