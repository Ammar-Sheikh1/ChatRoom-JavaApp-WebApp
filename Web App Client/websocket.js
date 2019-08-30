const connection = new WebSocket('ws://localhost:8080');
var user;

connection.onopen = () => {
  console.log('connected');
};

connection.onclose = () => {
  console.error('disconnected');
};

connection.onerror = (error) => {
  console.error('failed to connect', error);
};

connection.onmessage = (event) => {
  console.log('received', JSON.parse(event.data));
  var resObj = JSON.parse(event.data);
  let type = resObj["Type"]; 

  if(type == "message"){
    displayMessage(resObj["Username"],resObj["Message"])
  }
  else if(type == "userInfo"){
    appendUser(resObj["Username"]);
  }
  else if(type == "groupInfo"){

  }
  else if(type == "verify"){
    verifyAndNotify(resObj["Username"],resObj["Verify"])
  }  
 
};

document.getElementById("sendBtn").addEventListener("click", function(){
  var message = document.querySelector("#messageBar").value;
  var doc = document.getElementById("userSelect");
  var receiver = doc.options[doc.selectedIndex].text;
  var obj = {
    "Type": "message",
    "Username": user,
    "Message" : message,
    "Receiver" : receiver,
  };
  connection.send(JSON.stringify(obj));
});

document.getElementById("loginBtn").addEventListener("click", function(){
  var username = document.getElementById("usernameText").value;
  var password = document.getElementById("passwordText").value;
  var obj = {
    "Type": "verify",
    "Username": username,
    "Password" : password
  };
  console.log("sending: " + JSON.stringify(obj));
  connection.send(JSON.stringify(obj));
});


function appendUser(user){
  console.log("appending" + user + " to select list");
  var a = document.createElement("OPTION");
  a.value = user;
  var b = document.createTextNode(user);
  a.appendChild(b);
  document.getElementById("userSelect").appendChild(a);
}

function displayMessage(sender,message){
  console.log(sender + " says " + message);
  var textArea = document.getElementById("messageArea");
  var textNode = document.createTextNode(sender+": "+message);
  textArea.appendChild(textNode);
}

function appendGroup(){

}

function verifyAndNotify(username, isVerified){
   var header = document.getElementById("headerText");
  if(isVerified == "true"){
    console.log("Logged in as " + username);
    header.innerText = username;
    user = username;
  }
  else{
    console.log("Invalid/Taken username or password");
    header.innerText = "Invalid!";
    }
  }




