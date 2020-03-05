import {ChannelRestPaginationService, BotRestPaginationService, MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {ChannelMessageView} from "/js/workspace-page/components/messages/ChannelMessageView.js";
import {UserRestPaginationService} from "../../../rest/entities-rest-pagination.js";

export class ChannelView {
    default_channel = null;

    constructor() {
        this.user_service = new UserRestPaginationService();
        this.channel_service = new ChannelRestPaginationService();
        this.message_service = new MessageRestPaginationService();
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
        this.addCurrentUser(loggedUser);
        return this;
    }

    showAllChannels(workspace_id) {
        this.addCurrentWorkspace(workspace_id);
        this.setLocalStorageSettings(0);
        this.channel_service.getChannelsByWorkspaceAndUser(workspace_id, this.loggedUser.id).then(
            channels => {
                if (channels.length > 0) {
                    this.addChannels(channels);
                }
            }
        );
        this.showBots(workspace_id);
    }

    selectFirstSidebarButton() {
        $('.p-channel_sidebar__channel')
            .first()
            .find('button')
            .click();
    }

    showBots(workspace_id) {
        $('#bot_representation').empty(); //чтоб не добавлялись лишние боты
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
                <div class="p-channel_sidebar__channel">
                    <button class="p-channel_sidebar__name_button">
                        <i class="p-channel_sidebar__channel_icon_circle">●</i>
                        <span class="p-channel_sidebar__name-3">
                            <span>` + bot['nickName'] + `</span>
                        </span>
                    </button>
                    <button class="p-channel_sidebar__close cross">
                        <i class="p-channel_sidebar__close__icon">✖</i>
                    </button>
                </div>
            </div>`
        );
    }

    addChannels(channels) {
        $('#id-channel_sidebar__channels__list').empty();
        $.each(channels, (idx, chn) => {
            if (!chn.isArchived && this.checkPrivacy(chn)) {
                this.addChannelIntoSidebarChannelList(chn);
                if (this.default_channel === null) {
                    this.default_channel = chn;
                } else {
                    this.message_service.getUnreadMessageInChannelForUser(chn.id, this.loggedUser.id).then(messages => {
                        if (messages.length > 0) {
                            this.enableChannelHasUnreadMessage(chn.id);
                        }
                    });
                }
            }
        });
    }

    addCurrentWorkspace(workspace) {
        $('#WorkspaceName').append(workspace);
    }

    addCurrentUser(user) {
        $('#WorkspaceUsername').append(user.name);
    }

    addChannelIntoSidebarChannelList(channel) {
        const chn_symbol = channel.isPrivate ? "🔒" : "#";

        if (!channel.isApp) {
            $('#id-channel_sidebar__channels__list').append(`
            <div class="p-channel_sidebar__channel">
                <button class="p-channel_sidebar__name_button" id="channel_button_${channel.id}" value="${channel.id}">
                    <i class="p-channel_sidebar__channel_icon_prefix">${chn_symbol}</i>
                    <span class="p-channel_sidebar__name-3" id="channel_name_${channel.id}">${channel.name}</span>
                </button>
            </div>`);
        } else {
            $('#id-app_sidebar__apps__list')
                .append(`<div class="p-channel_sidebar__channel">
                                    <button class="p-channel_sidebar__name_button" id="channel_button_${channel.id}" value="${channel.id}">
                                        <i class="p-channel_sidebar__channel_icon_circle">●</i>
                                        <span class="p-channel_sidebar__name-3" id="channel_name_${channel.id}">${channel.name}</span>
                                    </button>
                                  </div>`
                );
        }
    }

    setLocalStorageSettings(chn_id) {
        sessionStorage.setItem('channelId', chn_id);
        sessionStorage.setItem('conversation_id', '0');
        window.channel_id = chn_id;
    }

    selectChannel(id) {
        this.channel_message_view.update().then(() => {
            this.setLocalStorageSettings(id);
            this.disableChannelHasUnreadMessage(id);
        });
    }

    checkPrivacy(channel) {
        return (channel.isPrivate && this.loggedUser.id === channel.ownerId) || !channel.isPrivate;
    };

    enableChannelHasUnreadMessage = (chnId) => {
        document.querySelector(`span#channel_name_${chnId}`).classList.add("font-weight-bold");
        document.querySelector(`span#channel_name_${chnId}`).classList.add("text-white");
    };

    disableChannelHasUnreadMessage = (chnId) => {
        document.querySelector(`span#channel_name_${chnId}`).classList.remove("font-weight-bold");
        document.querySelector(`span#channel_name_${chnId}`).classList.remove("text-white");
    }
}

export const deleteChannelFromList = (targetChannelId) => {
    document.querySelectorAll("[id^=channel_button_]").forEach(id => {
        if (id.value === targetChannelId) {
            id.parentElement.remove();
            if (window.channel_id === targetChannelId) {
                window.pressChannelButton(document.querySelectorAll("[id^=channel_button_]").item(0).value)
            }
        }
    })
};

