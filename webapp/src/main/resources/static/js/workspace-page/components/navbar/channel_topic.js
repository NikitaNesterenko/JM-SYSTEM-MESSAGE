import {ChannelTopicView} from "./ChannelTopicView.js";

const get_topic_channel = new ChannelTopicView();

window.addEventListener('load', function () {
    get_topic_channel.buildEvents();
});

$(document).ready(() => {
   get_topic_channel.setTopic();
});

