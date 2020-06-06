var stompClient = null;

let searchPerson = document.getElementById("search-person");
let foundUsers = document.getElementById("found-users-list");
let selectedUsers = document.getElementById("selected-container");
let usersForm = document.forms.userForm;

let chatContainer = document.getElementById("chat-container");
let noSelectionContainer = document.getElementById("no-selection-container");
let convos = document.getElementsByClassName("conversation-selection");
let currentChat = document.getElementById("open-chat");

let openConvo;
let prevConvo;

let selectedUsersList = [];

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
       stompClient.subscribe('/user/topic/chat', function(message) {
           //un-hide the chat container
           noSelectionContainer.style.display = "none";
           chatContainer.style.display = "grid";
           // console.log(message.body.chatId);
           console.log(message.body);
           openConvo = JSON.parse(message.body);

           clearChat();

           loadMessages(openConvo.otherUser);
       });

       stompClient.subscribe('/user/queue/sent-message', function(message) {
           console.log(message.body);
           createSentMessage(JSON.parse(message.body));
       });

       stompClient.subscribe('/user/queue/receive-message', function(message) {
           let receivedMessage = JSON.parse(message.body);
           if(receivedMessage.chatId === openConvo.chatId){
               createReceiveMessage(receivedMessage, openConvo.otherUser);
           }
          console.log(message.body);
       });

        for(let convo of convos) {
            convo.addEventListener("click", getConversation, false);
        }
    });
}

/**
 * CHAT CREATION
 */
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

        userElement.addEventListener("click", selectUser, false);
        userElement.user = user; //we added the user object to the element's proto
        foundUsers.append(userElement); //add the found user to the list
    }
}

//event listeners pass the current target as its parameter
function selectUser(userElement) {

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
            addedUser.addEventListener("click", removeFromSelection);
            selectedUsers.append(addedUser); //add the user to the document
            selectedUsersList.push(user.userId); //add to the list to be submitted
        }
}

function removeFromSelection(userElement) {
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
        document.getElementById("user"+userId).addEventListener("click", selectUser); //re-enable the addUser event
    } catch (e) {
        console.log("This user is no longer in the search results: "+e);
    }
}

function createChat() {
    usersForm.elements.users.value = selectedUsersList;
    console.log(usersForm.elements.users.value);
    usersForm.submit();
}

/**
 * MESSAGING FUNCTIONALITY
 */

function clearChat() {
    if(currentChat.firstChild != null) {
        while(currentChat.firstChild) {
            currentChat.removeChild(currentChat.lastChild);
        }
    }
}

const filterId = function(stringId) {
    return stringId.split("").filter(n => {
        return !isNaN(n);
    }).join("");
};

function getConversation(convo) {

    let currentConvo = convo.currentTarget.id;
    document.getElementById(currentConvo).classList.add("open-convo");
    if(prevConvo != null){
        document.getElementById(prevConvo).classList.remove("open-convo");
    }
    prevConvo = currentConvo;

    let convoId = filterId(currentConvo);
    console.log("Opening Chat: "+convoId);
    stompClient.send("/app/open-chat", {}, (convoId));
}

async function loadMessages(otherUser) {

    document.getElementById("header-name").innerText = otherUser.name;
    document.getElementById("header-username").innerText ="@"+otherUser.username;

    const response = await fetch("http://localhost:8080/messages/api-v1/chat-messages");
    let chatMessages = await response.json();
    console.log(chatMessages);
    // let openChat = document.getElementById("open-chat");

    for(let message of chatMessages) {
        if(message.received === true) {
            //create the received div
            createReceiveMessage(message, otherUser);
        } else {
            //create the sent div
            createSentMessage(message);
        }
    }

}


let messageInput = document.getElementById("message-input");
let sendButton = document.getElementById("send-button");
sendButton.addEventListener("click", sendMessage, false);

function sendMessage() {
    let message = {
        chatId: openConvo.chatId,
        content: messageInput.value,
    };
    console.log(JSON.stringify(message));
    stompClient.send("/app/send", {}, JSON.stringify(message));
    messageInput.value = null;
}


function createSentMessage(message) {
    // console.log("creating message dynamically: "+message.content);

    let sentMessage = document.createElement("div");
    let messageContainer = document.createElement("div");
    let messageContent = document.createElement("p");
    let messageDate = document.createElement("p");

    sentMessage.className = "user-message";
    messageContainer.className = "message-container";
    messageContent.className = "sent-content";
    messageContent.innerText = message.content;

    messageDate.innerText = message.date;
    messageContainer.append(messageContent);

    sentMessage.append(messageContainer, messageDate);
    // currentChat.append(sentMessage);
    insertAfter(sentMessage);
    updateRecent(message);
}

function createReceiveMessage(message, otherUser) {
    // console.log("receiving message dynamically: "+message.content+"//"+otherUser);

    let receiveMessage = document.createElement("div");
    let otherUserElement = document.createElement("a");
    let userPfp = document.createElement("img");
    let messageContainer = document.createElement("div");
    let messageContent = document.createElement("p");
    let messageDate = document.createElement("p");

    receiveMessage.className = "receive-message";
    otherUserElement.href = "#";
    userPfp.src = otherUser.pfp;
    otherUserElement.append(userPfp);
    messageContainer.className = "message-container";
    messageContent.className = "received-content";
    messageContent.innerText = message.content;
    messageContainer.append(messageContent);
    messageDate.innerText = message.date;

    receiveMessage.append(otherUserElement, messageContainer, messageDate);
    // currentChat.append(receiveMessage);
    insertAfter(receiveMessage);
    updateRecent(message);
}

function insertAfter(newNode) {
    if(currentChat.firstChild === null) {
        currentChat.append(newNode);
    }
        let referenceNode = currentChat.firstChild;
        referenceNode.parentNode.insertBefore(newNode, referenceNode);
}

function updateRecent(message) {
    console.log("updating recent to: "+message);
    let convo = document.getElementById("chat"+message.chatId);
    let date = convo.getElementsByClassName("message-date")[0];
    let recent = convo.getElementsByClassName("recent-message")[0].getElementsByTagName("p")[0];

    date.innerText = message.date;
    recent.innerText = message.content;
}

$(function () {
    //prevents the page from refreshing on button click
    $("#send-button").on('click', function (e) {
        e.preventDefault();
    });
    $( "#new-chat-button" ).click(function() { createChat();});
});