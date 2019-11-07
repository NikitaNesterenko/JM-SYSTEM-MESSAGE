import {getChannelTopic, updateChannelTopic} from './ajax/channelTopicRestController.js';

window.addEventListener('load', function () {
    // id канала где брать??
    let channelId = 1; // hardcoded id
    let topic = getChannelTopic(channelId);
    let topic_text = topic == "" ? "Enter channel topic here." : topic;

    $("#topic_string").text(topic_text);

    $("#topic_string_block").on("mouseover mouseout", function() {
        $("#topic_button").toggle();
    });

    $('#topic_button').on('click', function () {
       let newTopic = prompt("Please provide topic for the channel:", topic_text);
       updateChannelTopic(channelId, newTopic);
        $("#topic_string").text(newTopic);
    });
});

