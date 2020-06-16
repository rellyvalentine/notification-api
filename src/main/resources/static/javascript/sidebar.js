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