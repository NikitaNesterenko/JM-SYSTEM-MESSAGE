import {ChannelTopicRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class ChannelTopicView {
    channel_topic;

    constructor() {
        this.channel_topic_service = new ChannelTopicRestPaginationService();
    }

    buildEvents() {
        this.onChannelTopicMouseOver();
        this.onClickChannelEdit();
    }

    onChannelTopicMouseOver() {
        $(document).on("mouseover mouseout", "#topic_string_block", function () {
            $("#topic_button").toggle();
        });
    }

    onClickChannelEdit() {
        $(document).on('click', '#topic_button', () => {
            this.updateTopic();
        });
    }

    checkTopic(topic) {
        return topic === "" || topic === "null" ? "Enter channel topic here." : topic;
    }

    async updateTopic() {
        const id = sessionStorage.getItem("channelId");
        await this.channel_topic_service.getChannelTopic(id).then(old_topic => {
                this.channel_topic = old_topic;
        });
        const newTopic = prompt("Please provide topic for the channel:", this.channel_topic);
        if (newTopic != null) {
            this.channel_topic_service.updateChannelTopic(id, newTopic).then(chn_topic => {
                $("#topic_string").text(this.checkTopic(chn_topic));
                console.log(id);
                console.log(newTopic);
                sendChannelTopicChange(id, newTopic);
            });
        }
    }
}