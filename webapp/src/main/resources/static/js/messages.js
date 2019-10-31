let stompClient = null;

function connect() {
    let socket = new SockJS('/websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            showMessage(JSON.parse(message.body).content);
        });
    });
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    console.log("Disconnected");
}

function sendName(message) {
    stompClient.send("/app/message", {}, JSON.stringify({
        'inputMassage': message.content,
        'dateCreate': message.dateCreate,
        'user': message.user
    }));
}

function showMessage(message) {
    $("#messages").append("<tr><td><hr><br>" + message + "</td></tr>");
}

connect();
window.pushMessage = function pushMessage(message) {
    sendName(message);
}
