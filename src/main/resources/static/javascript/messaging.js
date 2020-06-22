var stompClient = null;
let openConvo;
let prevConvo;

connect().then();

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

       //search for users
       stompClient.subscribe('/user/topic/found-users', function(message) {
          console.log(message);
          let users = JSON.parse(message.body);
          showFoundUsers(users);
       });

       //opening a chat
       stompClient.subscribe('/user/topic/chat', function(message) {
           let chatContainer = document.getElementById("chat-container");
           let noSelectionContainer = document.getElementById("no-selection-container");
           //un-hide the chat container
           noSelectionContainer.style.display = "none";
           chatContainer.style.display = "grid";

           // console.log(message.body);
           openConvo = JSON.parse(message.body);

           clearChat();

           loadMessages(openConvo.otherUser);
       });

       stompClient.subscribe('/user/queue/read-message', function(message){
          updateMessageBell(message.body);
       });

       //sending messages
       stompClient.subscribe('/user/queue/sent-message', function(message) {
           console.log(message.body);
           createSentMessage(JSON.parse(message.body));
       });

       //receiving messages
       stompClient.subscribe('/user/queue/receive-message', function(message) {
           let receivedMessage = JSON.parse(message.body);
           if(receivedMessage.chatId === openConvo.chatId){
               createReceiveMessage(receivedMessage, openConvo.otherUser);
           }
          console.log(message.body);
       });

       //once convos are loaded, add an event listener to open them
        let convos = document.getElementsByClassName("conversation-selection");
        for(let convo of convos) {
            convo.addEventListener("click", getConversation, false);
        }

        stompClient.subscribe('/user/queue/bell-new', function (message) {
            console.log("bell reached"+message);
            updateBell(message.body);
        });
    });
}

/**
 * MESSAGING FUNCTIONALITY
 */

let currentChat = document.getElementById("open-chat");

/**
 * Clear the chat container from a previously opened chat
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

/**
 * Open an available conversation
 * Close the previously opened conversation
 * @param convo
 */
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

/**
 * load the messages from the selected chat
 *
 * @param otherUser
 * @returns {Promise<void>}
 */
async function loadMessages(otherUser) {

    document.getElementById("header-name").innerText = otherUser.name;
    document.getElementById("header-username").innerText ="@"+otherUser.username;



    const response = await fetch("http://localhost:8080/messages/api-v1/chat-messages?chatId="+openConvo.chatId);
    let chatMessages = await response.json();

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

/**
 * Update the most recent message in the Convo container
 * @param message
 */
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
});