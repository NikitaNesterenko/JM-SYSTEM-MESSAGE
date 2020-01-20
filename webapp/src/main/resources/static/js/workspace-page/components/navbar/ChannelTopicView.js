import {GetChannelTopic} from "/js/ajax/channelTopicRestController/getChannelTopic.js";
import {UpdateChannelTopic} from "/js/ajax/channelTopicRestController/updateChannelTopic.js";

export class ChannelTopicView {
    channel_topic;
    channel_id;

    constructor() {
        this.channel_id = sessionStorage.getItem("channelName");
    }

    buildEvents() {
        this.onClickChannelSelection();
        this.onChannelTopicMouseOver();
        this.onClickChannelEdit();
    }

    onClickChannelSelection() {
        $(".p-channel_sidebar__channels__list").on("click", "button.p-channel_sidebar__name_button", () => {
            this.channel_id = sessionStorage.getItem("channelName");
            this.setTopic();
        })
    }

    onChannelTopicMouseOver() {
        $("#topic_string_block").on("mouseover mouseout", function () {
            $("#topic_button").toggle();
        });
    }

    onClickChannelEdit() {
        $('#topic_button').on('click', () => {
            this.updateTopic();
        });
    }

    setTopic() {
        if (this.channel_id != 0 && this.channel_id != null) {
            const chn_topic = new GetChannelTopic(this.channel_id);
            this.channel_topic = this.checkTopic(chn_topic.getChannelTopic());
            $("#topic_string").text(this.channel_topic);
        }
    }

    checkTopic(topic) {
        return topic === "" || topic === null ? "Enter channel topic here." : topic.replace(/"/g, '');
    }

    updateTopic() {
        const newTopic = prompt("Please provide topic for the channel:", this.channel_topic);
        const chn_topic = new UpdateChannelTopic(this.channel_id, newTopic);
        chn_topic.updateChannelTopic();
        this.channel_topic = newTopic;
        $("#topic_string").text(newTopic);
    }
}