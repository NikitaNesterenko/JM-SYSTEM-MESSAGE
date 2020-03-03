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

const notifyParseMessage = (message) => {
    const messageFrom = message.user.name;
    const messageContent = message.content;
    if (messageContent.includes("@",0)) {
        const indexToStart = messageContent.indexOf("@");
        let indexToEnd = messageContent.indexOf(" ",indexToStart);
        if (indexToEnd === -1) {
            indexToEnd = messageContent.length;
        }
        const messageTo = messageContent.substring(indexToStart, indexToEnd);
        notify("Message to " + messageTo, messageFrom.name + ": " + messageContent);
    }
};

