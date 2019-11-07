const sound = new Audio("/audio/push.mp3");
const pic = "/image/img.JPG";

const notify = (title, message) => {
    Push.create(title, {
        body: message,
        timeout: 10000,
        icon: pic
    })
    sound.play();
}

function testNote() {
    notify("Message from MessageService", "My test message for you")
}

function testNote2() {
    notify("Message in Channel1", "Santa: Happy New Year!")
}