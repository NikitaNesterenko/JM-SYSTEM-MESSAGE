import {
    BotRestPaginationService,
    ChannelRestPaginationService,
    MessageRestPaginationService
} from "/js/rest/entities-rest-pagination.js";
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
            //sessionStorage.setItem('conversation_id', '0');
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
        sessionStorage.clear();
        //this.setLocalStorageSettings(0);
        //берем каналы для конкретного пользователя и конкретного воркспейса
        this.channel_service.getChannelsByWorkspaceAndUser(workspace_id, this.loggedUser.id).then(
            channels => {
                if (channels.length > 0) {
                    this.addChannels(channels);
                    if (this.default_channel !== null) {
                        //this.setLocalStorageSettings(this.default_channel.id);
                        this.setChannelBGColor(this.default_channel);
                        this.channel_message_view.update();
                    }
                }
            }
        );
        this.showBots(workspace_id);
    }

    setFlaggedItems() {
        $("#flaggedItems").append(0);
        alert(sessionStorage.getItem("channelId"))
    }

    showPeopleInChannel(channelId) {
        alert(sessionStorage.getItem("channelId"));
        const member_list = $('#memberListPlaceholder');
        this.user_service.getUsersByChannelId(sessionStorage.getItem("channelId")).then(
            users => {
                alert(users);
                member_list.empty();
                member_list.append(this.createMemberList(users));
            }
        );
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
        $('#id-channel_sidebar__channels__list').empty(); //обнулдяем список каналов перед заполнением
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

    setChannelBGColor(channel) {
        $(`#channel_button_${channel.id}`).css({
            color: 'white',
            background: 'royalblue'
        });
        $(".p-classic_nav__model__title__info__name").html("").text(channel.name);
    }

    setLocalStorageSettings(chn_id) { // закомментить
        sessionStorage.setItem('channelId', chn_id);
        sessionStorage.setItem('conversation_id', '0');
        window.channel_id = chn_id;
    }

    selectChannel(id) {
        this.channel_message_view.update().then(() => this.setLocalStorageSettings(id));
        $('.p-channel_sidebar__name_button').each((btn) => {
            let bg_color = {color: "rgb(188,171,188)", background: "none"};

            if ($(btn).filter(`[id=channel_button_${id}]`).length) {
                bg_color = {color: "white", background: "royalblue"};
            }

            $(btn).css(bg_color);
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

//удаление канала из списка каналов
export const deleteChannelFromList = (targetChannelId) => {
    document.querySelectorAll("[id^=channel_button_]").forEach(id => { //проверка, есть ли данный канал в существующем списке
        if (id.value == targetChannelId) {
            //удаляем канал из списка
            id.parentElement.remove();
            //если удаляемый канал был активен, то выбираем первый канал в списке
            if (window.channel_id == targetChannelId) {
                window.pressChannelButton(document.querySelectorAll("[id^=channel_button_]").item(0).value)
            }
        }
    })


};

