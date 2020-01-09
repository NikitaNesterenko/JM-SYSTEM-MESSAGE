import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {ChannelRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {updateAllMessages} from "./footer/messages.js";
import {BotRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {ChannelTopicView} from "./navbar/ChannelTopicView.js";

export class Channel {

    constructor() {
        const workspace_service = new WorkspaceRestPaginationService();
        this.workspace = workspace_service.getChoosedWorkspace();
        this.channel_service = new ChannelRestPaginationService();
        this.bot_service = new BotRestPaginationService();
    }

    setStorageSettings(channel) {
        sessionStorage.setItem('channelName', channel.id);
        sessionStorage.setItem('conversation_id', '0');
        window.channel_id = channel.id;
        updateAllMessages();
    }

    showAllChannels() {
        this.workspace.then(
            workspace => {
                this.channel_service.getChannelsByWorkspaceId(workspace.id).then(
                    channels => {
                        if (channels.length > 0) {
                            this.setStorageSettings(channels[0]);
                            this.addChannels(channels);
                            this.setDefaultChannel(channels[0]);
                            this.setChannelTopic();
                        }
                    }
                );
                this.showBots(workspace.id);
            }
        )
    }

    showBots(workspace_id) {
        this.bot_service.getBotByWorkspaceId(workspace_id).then(
            bot => {
                if (bot !== undefined) {
                    this.addBot(bot);
                }
            }
        );
    }

    addChannels(channels) {
       $.each(channels, (idx, chn) => this.addChannel(chn));
    }

    addChannel(channel) {
        const chn_symbol = channel.isPrivate ? "🔒" : "#";
        $('#id-channel_sidebar__channels__list').append(`
            <div class="p-channel_sidebar__channel">
                    <button class="p-channel_sidebar__name_button" id="channel_button_${channel.id}" value="${channel.id}">
                        <i class="p-channel_sidebar__channel_icon_prefix">${chn_symbol}</i>
                        <span class="p-channel_sidebar__name-3" id="channel_name">${channel.name}</span>
                    </button>
            </div>`);
    }

    addBot(bot) {
        $('#bot_representation').append(
            `<div class="p-channel_sidebar__direct-messages__container">
                <div class="p-channel_sidebar__close_container">
                    <button class="p-channel_sidebar__name_button">
                        <i class="p-channel_sidebar__channel_icon_circle">●</i>
                        <span class="p-channel_sidebar__name-3">
                            <span>` + bot['nickName'] + `</span>
                        </span>
                    </button>
                    <button class="p-channel_sidebar__close">
                        <i class="p-channel_sidebar__close__icon">✖</i>
                    </button>
                </div>
            </div>`
        );
    }

    setDefaultChannel(channel) {
        $("#channel_button_" + channel.id).css({
            color: 'white',
            background: 'royalblue'
        })
    }

    setChannelTopic() {
        const get_topic_channel = new ChannelTopicView();
        get_topic_channel.setTopic();
    }

    selectChannel(id) {
        this.workspace.then(
            wks => this.channel_service.getChannelsByWorkspaceId(wks.id).then(
                channels => {
                    channels.forEach(function (channel, i) {
                        if (id !== channel.id) {
                            $(`#channel_button_${channel.id}`).css({
                                color: "rgb(188,171,188)",
                                background: "none"
                            });
                        }
                    })
                }
            )
        );

        $(`#channel_button_${id}`).css({
            color: "white",
            background: "royalblue"
        });

        window.channel_id = id;
    }
}