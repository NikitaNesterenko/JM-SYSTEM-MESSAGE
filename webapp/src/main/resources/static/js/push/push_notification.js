import {SendNotification} from '../rest/entities-rest-pagination.js'
const personalNotification = new SendNotification();
const pic = "/image/img.JPG";

window.sendNotification = function notifyParseMessage(message) {
    const messageFrom = message.user;
    const messageContent = message.inputMassage;
    const messageChannel = message.channel;
    if (messageContent.includes("@",0)) {
        let name = messageContent.substr(1);
        if(name === 'channel'){
            let pushNotify = {"title":"From channel: " + messageChannel.name, "body":"test", "icon": pic, "click_action":"google.com", "ttlInSeconds":"20"};
            personalNotification.sendToChannelMembers(messageChannel.id, pushNotify)
        } else {
            let pushNotify = {"title":"From: " + messageFrom.name, "body":"test", "icon": pic, "click_action":"google.com", "ttlInSeconds":"20"};
            personalNotification.sendPersonal(name, pushNotify);
        }
    }
};



