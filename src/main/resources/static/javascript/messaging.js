var stompClient = null;

let selectedContainer =  document.getElementById("selected-container");
let searchPerson = document.getElementById("search-person");

connect();

async function connect() {

    const response = await fetch("http://localhost:8080/csrf");
    let data = await response.json();
    console.log(data);

    let headers = {};
    headers[data.headerName] = data.token;
    console.log(headers);

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect(headers, function(frame) {
       console.log("Connected: "+frame);

       stompClient.subscribe('/user/topic/found-users', function(message) {
          console.log(message);
       });
    });
}

//search for users
searchPerson.oninput = function(e) {
  let s = e.target.value;
  if(s !== ""){ //if the input is not empty, we'll search for users
      stompClient.send("/app/search-users", {}, s);
  }
};