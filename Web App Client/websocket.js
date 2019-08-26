const connection = new WebSocket('ws://localhost:8080');

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
  console.log('received', event.data);
  let li = document.createElement('li');
  li.innerText = event.data;
  document.querySelector('#chat').append(li);
  var obj = JSON.parse(event.data);
  let li2 = document.createElement('li');
  li2.innerText = obj["Verify"];
  document.querySelector('#chat').append(li2);

 /* var obj = JSON.parse(event.data);
  let li2 = document.createElement('li');
  li2.innerText = obj["Type"];
  let li3 = document.createElement('li');
  li3.innerText = obj["Verify"];
  document.querySelector('#chat').append(li2);
  document.querySelector('#chat').append(li3);
*/


};

document.querySelector('form').addEventListener('submit', (event) => {
  event.preventDefault();
  var json = {
    "Type" : "Message"
  };

  let message = document.querySelector('#message').value;
  connection.send(message);
 //connection.send(JSON.stringify(json));
  document.querySelector('#message').value = '';
});


document.querySelector('form').addEventListener('loginBtn', (event) => {
  event.preventDefault();
  var user = document.querySelector('#username').value;
  var pass = document.querySelector('#password').value;
  var obj = {
    "Type": "verify",
    "Username" : user,
    "Password" : pass,
  }
  connection.send(JSON.stringify(obj));
    document.querySelector('#username').value = '';
    document.querySelector('#password').value = '';
});

