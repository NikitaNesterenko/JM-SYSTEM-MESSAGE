import {ChannelTopicRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class ChannelTopicView {
    channel_topic;
    channel_id;

    constructor() {
        this.channel_id = sessionStorage.getItem("channelName");
        this.channel_topic_service = new ChannelTopicRestPaginationService();
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
        $(document).on("mouseover mouseout", "#topic_string_block", function () {
            $("#topic_button").toggle();
        });
    }

    onClickChannelEdit() {
        $(document).on('click', '#topic_button', () => {
            this.updateTopic();
        });
    }

    setTopic() {
        if (this.channel_id != 0 && this.channel_id != null) {
            this.channel_topic_service.getChannelTopic(this.channel_id).then(chn_topic => {
                if (chn_topic !== null && chn_topic !== undefined) {
                    this.channel_topic = this.checkTopic(chn_topic);
                    $("#topic_string").text(this.channel_topic);
                }
            });
        }
    }

    checkTopic(topic) {
        return topic === "" || topic === "null" ? "Enter channel topic here." : topic.replace(/"/g, '');
    }

    updateTopic() {
        const newTopic = prompt("Please provide topic for the channel:", this.channel_topic);
        if (newTopic != null) {
            this.channel_topic_service.updateChannelTopic(this.channel_id, newTopic).then(chn_topic => {
                $("#topic_string").text(this.checkTopic(chn_topic));
            });
        }
    }


}