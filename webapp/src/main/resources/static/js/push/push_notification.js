const sound = new Audio("/audio/push.mp3");
const pic = "/image/img.JPG";

class PushNotification {
    constructor(title, message) {
        this.title = title;
        this.message = message;
    }



    notify(){
        Push.create(this.title, {
            body: this.message,
            timeout: 10000,
            icon: pic
        });
        sound.play();
    }

// Парсер для сообщения типа "{inputMassage: "@asdas", dateCreate: "14.11.2019 18:07", user: {…}, bot: null}"
// user {id: 2, name: "name_2", lastName: "last-name_2", login: "login_2", email: "mymail_2@testmail.com", …}
    notifyParseMessage() {
        const messageFrom = this.message.user;
        const messageContent = this.message.inputMassage;
        if (messageContent.includes("@",0)) {
            const indexToStart = messageContent.indexOf("@");
            let indexToEnd = messageContent.indexOf(" ",indexToStart);
            if (indexToEnd == -1) {
                indexToEnd = messageContent.length;
            }
            const messageTo = messageContent.substring(indexToStart, indexToEnd);
            notify("Message to " + messageTo, messageFrom.name + ": " + messageContent);
        }
    }
}