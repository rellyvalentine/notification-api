var stompClient = null;
const userData = document.getElementById("p1");

function setConnected(connected) {
    $("#connect").prop("disabled", connected); //when connected = true, button is disabled
    $("#disconnect").prop("disabled", !connected) //when connected = false, button is disabled
}




async function connect() {


    const response = await fetch("http://localhost:8080/csrf");
    let data = await response.json();
    console.log(data);

    let headers = {};
    headers[data.headerName] = data.token;
    console.log(headers);


    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

        stompClient.connect(headers, function (frame) {

            setConnected(true);
            console.log("Connected: "+frame);

            stompClient.subscribe('/user/topic/random-user', function (message) {
                console.log(message);
                let user = JSON.parse(message.body);

                createNotification(user.userId);
                showUser(user.username);
            });

            stompClient.subscribe('/user/queue/bell', function (message) {
                console.log("bell reached"+message);
                updateBell(message.body);
            });
        });

}


function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function createNotification(id){
    stompClient.send("/app/new-notif", {}, id);
}

function showUser(user) {
    console.log("user being shown");
    console.log(user);
    userData.innerHTML = "Retrieved Random User: "+user;
}

function getUser() {
    console.log("send executed");
    stompClient.send("/app/get-person", {});
}

function updateBell(size) {

    let blankNotif = document.getElementById("blankNotif");
    let notifNumber = document.getElementById("notifNumber");

    console.log("size: "+size);
    if(notifNumber === null){ //if the page hasn't been refreshed, keep updating blank notif
        blankNotif.innerHTML = size;
    } else {
        notifNumber.innerHTML = size;
    }
}


$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#getUser" ).click(function() { getUser();});
});