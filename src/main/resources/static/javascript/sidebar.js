function updateBell(size) {

    let blankNotif = document.getElementById("no-notif");
    let notifNumber = document.getElementById("notif-number");

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

function updateMessageBell(size){
    console.log(size);
    let blankMessage = document.getElementById("no-message");
    let messageNumber = document.getElementById("message-number");

    if(messageNumber === null){
        if(size === '0'){
            blankMessage.style.display = "none";
        } else{
            blankMessage.style.display = "flex";
            blankMessage.innerHTML = size;
        }

    } else {
        if(size === '0'){
            messageNumber.style.display = "none";
        } else{
            messageNumber.style.display = "flex";
            messageNumber.innerHTML = size;
        }
    }

}