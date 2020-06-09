'use strict';


var messageForm = document.querySelector('#messageForm');
var raiseHandForm = document.querySelector('#raiseHand');
var putHandForm = document.querySelector('#putHand');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('#connecting');



var stompClient = null;
var username = null;


function connect() {
    username = document.querySelector('#username').innerText.trim();

    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

// Connect to WebSocket Server.
connect();


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/publicChatRoom', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    stompClient.send("/app/chat.allUsers",
        {},
        JSON.stringify({sender: username, type: 'CHAT'})
    );

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

function raiseHand(event) {
    var messageContent = 'raise his hand!';

    raiseHandForm.style.display = 'none';
    putHandForm.style.display = 'block';

    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'HAND_UP'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));

        setTimeout(() =>
            stompClient.send("/app/chat.allUsers",
                {},
                JSON.stringify({sender: username, type: 'CHAT'})
            ), 2000);

        event.preventDefault();
    }
}

function putHand(event) {
    var messageContent = 'put his hand down!';

    raiseHandForm.style.display = 'block';
    putHandForm.style.display = 'none';

    sessionStorage.setItem("hand", "down");
    if (messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageContent,
            type: 'HAND_DOWN'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));

        setTimeout(() =>
            stompClient.send("/app/chat.allUsers",
                {},
                JSON.stringify({sender: username, type: 'CHAT'})
            ), 2000);
    }
    event.preventDefault();
}

function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if (message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else if (message.type === 'USERS') {
        const table = document.querySelector('#tab_body');

        while (table.firstChild) {
            table.removeChild(table.lastChild);
        }

        const arr_stud = message.content.split('|');
        for (let i = 0; i < arr_stud.length; i++) {
            const name = arr_stud[i].split('&')[0];
            const hand = arr_stud[i].split('&')[1];

            if (name === username) {
                if (hand === 'UP') {
                    raiseHandForm.style.display = 'none';
                    putHandForm.style.display = 'block';
                } else {
                    raiseHandForm.style.display = 'block';
                    putHandForm.style.display = 'none';
                }
            }

            const tr = document.createElement('tr');
            let td = document.createElement('td');
            let text = document.createTextNode(name);

            td.appendChild(text);
            tr.appendChild(td);


            if (hand === 'UP') {
                td = document.createElement('td');
                td.setAttribute("id", "hand-user");
                text = document.createTextNode("\u270B");

                td.appendChild(text);
                tr.appendChild(td);
            }

            table.appendChild(tr);
        }

        return;
    } else {
        messageElement.classList.add('chat-message');
        var usernameElement = document.createElement('strong');
        usernameElement.classList.add('nickname');
        var usernameText = document.createTextNode(message.sender);
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('span');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


messageForm.addEventListener('submit', sendMessage, true);
raiseHandForm.addEventListener('submit', raiseHand, true);
putHandForm.addEventListener('submit', putHand, true);

