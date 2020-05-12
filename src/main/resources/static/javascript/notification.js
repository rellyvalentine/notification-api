//retrieve the notificationId from the HTML id
const filterId = function(stringId){
    return stringId.split("").filter(n => {
        return !isNaN(n); //filter out any non numbers in the array
    }).join(""); //combine the array of numbers
};

//updating if a notification has been seen/read
const newNotifications = document.getElementsByClassName("newNotif"); //retrieves all notifications

console.log(`Notifications: ${newNotifications.length}`);

for (let notification of newNotifications){
    console.log(`Notification: ${notification.id}`);

    // console.log(`Notification Id: ${notificationId}`);
    notification.addEventListener("click", () =>{
        console.log(`${notification.id} clicked`);
        const notificationId = filterId(notification.id); //get the Notification's id

        //get the badge from the HTML and hide it once clicked
        const badge = document.getElementById(`newBadge${notificationId}`);
        badge.hidden = true;

        const url = "http://localhost:8080/api-v1/read-notif";
        const xhr = new XMLHttpRequest();
        xhr.open("POST", url);
        xhr.setRequestHeader("Content-Type", "application/JSON");
        xhr.send(notificationId);

    });
}