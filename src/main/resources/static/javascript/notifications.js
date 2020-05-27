var stompClient = null;
let notifNumber = document.getElementById("notif-number");
let blankNotif = document.getElementById("no-notif");
const newNotifications = document.getElementsByClassName("new-notif");

connect();

async function connect() {
    const response = await fetch("http://localhost:8080/csrf");
    let data = await response.json();

    let headers = {};
    headers[data.headerName] = data.token;
    console.log(headers);

    var socket = new SockJS("/ws");
    stompClient = Stomp.over(socket);

        stompClient.connect(headers, function(frame) {
           console.log("Connected: "+frame);

           stompClient.subscribe('/user/queue/bell-read', function(message) {
                updateBell(message.body);
           });
        });
}

const filterId = function(stringId) {
  return stringId.split("").filter(n => {
    return !isNaN(n); //filter out non-number values
  }).join("");
};


    console.log(`Notifications: ${newNotifications.length}`);

    for(let notification of newNotifications) {
        notification.addEventListener('click', () =>{
            console.log(`${notification.id} clicked`);
            let notificationId = filterId(notification.id);
            let notifElement = document.getElementById(`newNotif${notificationId}`);
            notifElement.style.background = "rgb(21, 32, 43)";
            notifElement.onmouseover = function() {
                notifElement.style.background = "rgb(25, 39, 52)";
            };
            notifElement.onmouseout = function() {
                notifElement.style.background = "rgb(21, 32, 43)";
            };

            stompClient.send("/app/read-notif", {}, notificationId);
        });
    }

function updateBell(size) {

        console.log("size: "+size);

        if(notifNumber === null) {
            if(size === '0'){
                console.log("no new notifications");
                blankNotif.style.display = "none";
            }

            blankNotif.style.display = "flex";
            blankNotif.innerHTML = size;
        } else {
            if(size === '0'){
                notifNumber.style.display = "none";
            } else {
                notifNumber.innerHTML = size;
            }
        }
}

// function updateBell(size) {
//     let blankNotif = document.getElementById("no-notif");
//     let notifNumber = document.getElementById("notif-number");
//
//     console.log("size: "+size);
//     if(notifNumber === null){ //if the page hasn't been refreshed, keep updating blank notif
//         blankNotif.style.display = "flex";
//         blankNotif.innerHTML = size;
//     } else {
//         notifNumber.innerHTML = size;
//     }
// }
//
// //retrieve the notificationId from the HTML id
// const filterId = function(stringId){
//     return stringId.split("").filter(n => {
//         return !isNaN(n); //filter out any non numbers in the array
//     }).join(""); //combine the array of numbers
// };
//
// //updating if a notification has been seen/read
// const newNotifications = document.getElementsByClassName("new-notif"); //retrieves all notifications
//
// console.log(`Notifications: ${newNotifications.length}`);
//
// for (let notification of newNotifications){
//     console.log(`Notification: ${notification.id}`);
//
//     notification.addEventListener("click", () =>{
//         console.log(`${notification.id} clicked`);
//         const notificationId = filterId(notification.id); //get the Notification's id
//
//         //get the badge from the HTML and hide it once clicked
//         // const badge = document.getElementById(`newBadge${notificationId}`);
//         // badge.hidden = true;
//
//         document.getElementById(`newNotif${notificationId}`).style.background = "rgb(21, 32, 43)";
//
//         // const url = "http://localhost:8080/api-v1/read-notif";
//         // const xhr = new XMLHttpRequest();
//         // xhr.open("POST", url);
//         // xhr.setRequestHeader("Content-Type", "application/JSON");
//         // xhr.send(notificationId);
//
//     });
// }