import {ChannelRestPaginationService, BotRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {ChannelMessageView} from "/js/workspace-page/components/messages/ChannelMessageView.js";

export class ChannelView {
    default_channel = null;

    constructor() {
        this.channel_service = new ChannelRestPaginationService();
        this.channel_message_view = new ChannelMessageView();
        this.bot_service = new BotRestPaginationService();
        window.pressChannelButton = (id) => {
            window.channel_id = id;
            this.selectChannel(id);
        }
    }

    setLoggedUser(loggedUser) {
        this.loggedUser = loggedUser;
        this.channel_message_view.logged_user = loggedUser;
        return this;
    }

    showAllChannels(workspace_id) {
        this.setLocalStorageSettings(0);
        this.channel_service.getChannelsByWorkspaceId(workspace_id).then(
            channels => {
                if (channels.length > 0) {
                    this.addChannels(channels);
                    if (this.default_channel !== null) {
                        this.setLocalStorageSettings(this.default_channel.id);
                        this.setChannelBGColor(this.default_channel);
                        this.channel_message_view.update();
                    }
                }
            }
        );
        this.showBots(workspace_id);
    }

    showBots(workspace_id) {
        this.bot_service.getBotByWorkspaceId(workspace_id).then(
            bots => {
                if (bots !== undefined) {
                    bots.forEach(bot => {
                        this.addBot(bot);
                    });
                }
            }
        );
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

    addChannels(channels) {
        $.each(channels, (idx, chn) => {
            if (!chn.isArchived && this.checkPrivacy(chn)) {
                if (this.default_channel === null) {
                    this.default_channel = chn;
                }
                this.addChannelIntoSidebarChannelList(chn);
            }
        });
    }

    addChannelIntoSidebarChannelList(channel) {
        const chn_symbol = channel.isPrivate ? "🔒" : "#";
        $('#id-channel_sidebar__channels__list').append(`
            <div class="p-channel_sidebar__channel">
                <button class="p-channel_sidebar__name_button" id="channel_button_${channel.id}" value="${channel.id}">
                    <i class="p-channel_sidebar__channel_icon_prefix">${chn_symbol}</i>
                    <span class="p-channel_sidebar__name-3" id="channel_name_${channel.id}">${channel.name}</span>
                </button>
            </div>`);
    }

    setChannelBGColor(channel) {
        $(`#channel_button_${channel.id}`).css({
            color: 'white',
            background: 'royalblue'
        });
        $(".p-classic_nav__model__title__info__name").html("").text(channel.name);
    }

    setLocalStorageSettings(chn_id) {
        sessionStorage.setItem('channelName', chn_id);
        sessionStorage.setItem('conversation_id', '0');
        window.channel_id = chn_id;
    }

    selectChannel(id) {
        this.channel_message_view.update().then(() => this.setLocalStorageSettings(id));
        $('.p-channel_sidebar__name_button').each((idx, btn) => {
            let bg_color = {color: "rgb(188,171,188)", background: "none"};

            if ($(btn).filter(`[id=channel_button_${id}]`).length) {
                bg_color = {color: "white", background: "royalblue"};
            }

            $(btn).css(bg_color);
        });
    }

    checkPrivacy(channel) {
        return (channel.isPrivate && this.loggedUser.id === channel.ownerId) || !channel.isPrivate;
    }
}