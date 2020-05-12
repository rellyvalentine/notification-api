var stompClient = null;
const userData = document.getElementById("p1");

function setConnected(connected) {
    $("#connect").prop("disabled", connected); //when connected = true, button is disabled
    $("#disconnect").prop("disabled", !connected) //when connected = false, button is disabled
}



// function createHeader(){
//
//     // var header;
//     //
//     // let request = new XMLHttpRequest();
//     // request.open("GET", "http://localhost:8080/csrf");
//     // request.send();
//     // request.onload = () => {
//     //     if(request.status === 200){
//     //         console.log(JSON.parse(request.response));
//     //
//     //         let headerName = JSON.parse(request.response).headerName;
//     //         let token = JSON.parse(request.response).token;
//     //
//     //         header = {
//     //             headerName: headerName,
//     //             token: token
//     //         };
//     //
//     //         console.log(JSON.stringify(header));
//     //
//     //     } else {
//     //         console.log("no csrf");
//     //     }
//     // };
//
//
//
// }

// const getCsrfHeader = async () =>{
//
//     try{
//         const fetchHeaders = await fetch("http://localhost:8080/csrf");
//         return await fetchHeaders.json();
//     } catch (e){
//         console.log(e);
//     }
//
// };


function connect() {

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);


        stompClient.connect({}, function (frame) {

            setConnected(true);
            console.log("Connected: "+frame);

            stompClient.subscribe('/topic/random-user', function (message) {
                console.log(message);
                console.log(message.body);
                // createNotification(message.body);
                showUser(message.body);
            });

            stompClient.subscribe('/topic/bell', function (message) {
                console.log(message);
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

// function createNotification(id){
//     stompClient.send("/app/new-notif", {}, JSON.stringify({
//         head: "Random Access",
//         body: "You have been accessed by a random user",
//         userId: JSON.parse(id)
//     }));
// }

function showUser(user) {
    console.log("user being shown");
    var userObject = JSON.parse(user);
    console.log(userObject);
    userData.innerHTML = JSON.stringify(userObject);
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