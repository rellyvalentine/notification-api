var stompClient = null;
// const userData = document.getElementById("p1");
const retrievedUserName = document.getElementById("retrieved-username");
const retrievedName = document.getElementById("retrieved-name");
const retrievedPfp = document.getElementById("retrieved-pfp");
const retrievedUserButton = document.getElementById("retrieved-user-button");

// function setConnected(connected) {
//     $("#connect").prop("disabled", connected); //when connected = true, button is disabled
//     $("#disconnect").prop("disabled", !connected) //when connected = false, button is disabled
// }

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

        stompClient.connect(headers, function (frame) {
            console.log("Connected: "+frame);

            stompClient.subscribe('/user/topic/random-user', function (message) {
                console.log(message);
                let user = JSON.parse(message.body);

                createNotification(user.userId);
                showUser(user);
            });

            stompClient.subscribe('/user/queue/bell-new', function (message) {
                console.log("bell reached"+message);
                updateBell(message.body);
            });
        });

}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    // setConnected(false);
    console.log("Disconnected");
}

function createNotification(id){
    stompClient.send("/app/new-notif", {}, id);
}

function showUser(user) {
    console.log("user being shown");
    console.log(user);
    if(retrievedUserButton.style.display === "none"){
     retrievedUserButton.style.display = "block";
    }
    retrievedName.innerText = user.name;
    retrievedUserName.innerText = "@"+user.username;
    retrievedPfp.setAttribute("src", user.pfp);

    // userData.innerHTML = "Retrieved Random User: "+user;
}

function getUser() {
    console.log("send executed");
    stompClient.send("/app/get-person", {});
}

function updateBell(size) {

    let blankNotif = document.getElementById("no-notif");
    let notifNumber = document.getElementById("notif-number");

    console.log("size: "+size);
    if(notifNumber === null){ //if the page hasn't been refreshed, keep updating blank notif
        blankNotif.style.display = "flex";
        blankNotif.innerHTML = size;
    } else {
        notifNumber.innerHTML = size;
    }
}

$(function () {
    //prevents the page from refreshing on button click
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    // $( "#connect" ).click(function() { connect(); });
    // $( "#disconnect" ).click(function() { disconnect(); });
    $( "#getUser" ).click(function() { getUser();});
});