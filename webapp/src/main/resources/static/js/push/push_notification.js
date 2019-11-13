const sound = new Audio("/audio/push.mp3");
const pic = "/image/img.JPG";

function notify (title, message){
    Push.create(title, {
        body: message,
        timeout: 10000,
        icon: pic
    });
    sound.play();
}

// Парсер для сообщения типа "<b>login_2</b>   2019-11-11   14:10<br><tr><td>ads<br></td></tr>"
function notifyParseMessage(message) {
    const messageStr = message.toString();
    const messageFrom = messageStr.substring(3, messageStr.indexOf("</b>", 4));
    const messageContent = messageStr.substring(messageStr.indexOf("<td>",0) + 4, messageStr.indexOf("</td>",0) - 4);
    if (messageContent.includes("@",0)) {
        const indexToStart = messageContent.indexOf("@");
        let indexToEnd = messageContent.indexOf(" ",indexToStart);
        if (indexToEnd == -1) {
            indexToEnd = messageContent.length;
        }
        const messageTo = messageContent.substring(indexToStart, indexToEnd);
        notify("Message to " + messageTo, messageFrom + ": " + messageContent);
    }
}

