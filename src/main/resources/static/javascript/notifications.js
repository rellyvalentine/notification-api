var stompClient = null;
const newNotifications = document.getElementsByClassName("new-notif");

connect().then();

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