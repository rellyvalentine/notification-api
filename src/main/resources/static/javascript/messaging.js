var stompClient = null;

let selectedContainer =  document.getElementById("selected-container");
let searchPerson = document.getElementById("search-person");
let foundUsers = document.getElementById("found-users-list");
let selectedUsers = document.getElementById("selected-container");
let selectedUsersList = [];
let usersForm = document.forms.userForm;

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
          let users = JSON.parse(message.body);
          showFoundUsers(users);
       });
    });
}

//search for users
searchPerson.oninput = function(e) {
  let s = e.target.value;
  if(s !== ""){ //if the input is not empty, we'll search for users
      stompClient.send("/app/search-users", {}, s);
  } else {
      foundUsers.style.display = "none";
  }

};

function showFoundUsers(users) {
    //clear the list
    while(foundUsers.firstChild) {
        foundUsers.removeChild(foundUsers.lastChild);
    }

    foundUsers.style.display = "flex";

    for(let user of users) {

        console.log(user);

        let userElement = document.createElement("div");
        userElement.className = "found-user";
        userElement.id = "user"+user.userId;

        let pfp = document.createElement("img");
        let name = document.createElement("h5");
        name.className = "user-title";
        let username = document.createElement("p");

        pfp.src = user.pfp;
        name.innerText = user.name;
        username.innerText = "@"+user.username;

        userElement.append(pfp, name, username);

        userElement.addEventListener("click", addUser, false);
        userElement.user = user; //we added the user object to the element's proto
        foundUsers.append(userElement); //add the found user to the list
    }
}

//event listeners pass the current target as its parameter
function addUser(userElement) {

        let user = userElement.currentTarget.user; //the current target is passed as the param
        console.log(user);

        if(selectedUsersList.includes(user.userId)) { //if this user is not in the list
           console.log("user is already in the list");
        } else {
            selectedUsers.style.display = "flex";
            let addedUser = document.createElement("button");
            addedUser.className = "selected-person";
            addedUser.type = "button";
            addedUser.id = user.userId;

            let pfp = document.createElement("img");
            let name = document.createElement("p");
            let svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
            svg.setAttribute("viewBox", "0 0 24 24");
            let path = document.createElementNS("http://www.w3.org/2000/svg", "path");
            path.setAttribute("d", "M13.414 12l5.793-5.793c.39-.39.39-1.023 0-1.414s-1.023-.39-1.414 0L12 10.586 6.207 4.793c-.39-.39-1.023-.39-1.414 0s-.39 1.023 0 1.414L10.586 12l-5.793 5.793c-.39.39-.39 1.023 0 1.414.195.195.45.293.707.293s.512-.098.707-.293L12 13.414l5.793 5.793c.195.195.45.293.707.293s.512-.098.707-.293c.39-.39.39-1.023 0-1.414L13.414 12z");
            svg.appendChild(path);

            pfp.src = user.pfp;
            name.innerText = user.name;
            addedUser.append(pfp, name, svg); //create the addedUser button
            addedUser.addEventListener("click", removeUser);
            selectedUsers.append(addedUser); //add the user to the document
            selectedUsersList.push(user.userId); //add to the list to be submitted
        }
}

function removeUser(userElement) {
    let userId = parseInt(userElement.currentTarget.id);
    console.log("Removing user: "+userId);
    console.log("Index in array: "+selectedUsersList.indexOf(userId));
    selectedUsersList.splice(selectedUsersList.indexOf(userId), 1); //delete the value from the list

    //remove in html
    document.getElementById(userId.toString()).remove(); //remove the node

    //check if we need to hide the selectedUsers
    if(selectedUsersList.length === 0) {
        selectedUsers.style.display = "none";
    }

    try{
        document.getElementById("user"+userId).addEventListener("click", addUser); //re-enable the addUser event
    } catch (e) {
        console.log("This user is no longer in the search results: "+e);
    }

}

function createChat() {
    usersForm.elements.users.value = selectedUsersList;
    console.log(usersForm.elements.users.value);
    usersForm.submit();
}



$(function () {
    //prevents the page from refreshing on button click
    // $("form").on('submit', function (e) {
    //     e.preventDefault();
    // });
    // // $( "#connect" ).click(function() { connect(); });
    // // $( "#disconnect" ).click(function() { disconnect(); });
    $( "#new-chat-button" ).click(function() { createChat();});
});