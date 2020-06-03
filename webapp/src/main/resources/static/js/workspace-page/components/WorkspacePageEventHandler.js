import {refreshMemberList} from "/js/member-list/member-list.js";
import {
    ChannelRestPaginationService,
    ChannelTopicRestPaginationService,
    ConversationRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService
} from "/js/rest/entities-rest-pagination.js";
import {ActiveChatMembers} from "./sidebar/ActiveChatMembers.js";
import {NavHeader} from "./navbar/NavHeader.js";

export class WorkspacePageEventHandler {

    channel_id;
  
    constructor(logged_user, stompClient) {
        this.logged_user = logged_user;
        this.stomp_client = stompClient;
        this.addChannelModal = $("#addChannelModal");
        this.addDirectMessageModal = $("#addDirectMessageModal");
        this.addChannelBtn = $("#addChannelButton");
        this.addDirectMessage = $("#addDirectMessage");
        this.channel_service = new ChannelRestPaginationService();
        this.channel_topic_service = new ChannelTopicRestPaginationService();
        this.user_service = new UserRestPaginationService();
        this.wks_header = new NavHeader();
        this.user_service = new UserRestPaginationService();
        this.workspace_service = new WorkspaceRestPaginationService();
        this.conversation_serivce = new ConversationRestPaginationService();
        this.chat_members = new ActiveChatMembers();
    }

    onAddChannelClick() {
        this.addChannelBtn.click(() => {
            this.addChannelModal.css('display', 'block');
        });
    }

    onAddDirectMessageClick() {
        this.addDirectMessage.click(() => {
            new WorkspaceRestPaginationService().getChosenWorkspace().then(workspace => {
                this.user_service.getUsersByWorkspace(workspace.id).then(users => {
                        let data = "<div class=\"list-group\">\n";
                        users.forEach(user => {
                            data += "<a class=\"list-group-item list-group-item-action\">";
                            data += user.name.toString();
                            data += "</a>\n";
                        });
                        data += "</div>";
                        $('#addDirectMessageModal').find('#DirectMessageTo').html(data);
                    }
                );
                this.addDirectMessageModal.css('display', 'block');
            });
        });
    }

    onWindowClick() {
        $(window).click((event) => {
            if (event.target === this.addChannelBtn) {
                this.addChannelModal.css('display', 'none');
                this.addDirectMessageModal.css('display', 'none');
            }
        })
    }

    onSelectChannel() {
        $(".p-channel_sidebar__channels__list").on("click", "button.p-channel_sidebar__name_button", (event) => {
            this.wks_header.setChannelTitle($(event.currentTarget).find('i').text(), $(event.currentTarget).find('span').text()).setInfo();

            const channelId = $(event.currentTarget).val();
            pressChannelButton(channelId);

            sessionStorage.setItem("channelId", channelId);
            sessionStorage.setItem('conversation_id', '0');

            let channel;
            this.channel_service.getChannelById(channelId)
                .then(res => {channel = res})
                .then(() => archiveChannelBlock(channel.isBlock,channel.isArchived))
                .catch(console.log);
            function archiveChannelBlock(block, archive){
                if (block&&archive) {
                    $( "#form_message_input" ).prop( "disabled", true );
                    $( "#attach_file" ).prop( "disabled", true );
                    $( "#inputEmojiButton" ).prop( "disabled", true );
                    $( "#showAssociatedUsers" ).prop( "disabled", true );
                    $( "#voiceMessageBtn" ).prop( "disabled", true );
                }else {
                    $( "#form_message_input" ).prop( "disabled", false );
                    $( "#attach_file" ).prop( "disabled", false );
                    $( "#inputEmojiButton" ).prop( "disabled", false );
                    $( "#showAssociatedUsers" ).prop( "disabled", false );
                    $( "#voiceMessageBtn" ).prop( "disabled", false );
                }
            }

            this.channel_topic_service.getChannelTopic(channelId).then(topic => {
                this.user_service.getUsersByChannelId(channelId).then(users => {
                    this.wks_header.setInfo(channelId, users.length, 666, this.checkTopic(topic));
                });
            });
            refreshMemberList();
        });
    }

    onAddConversationSubmit() {
        $("#addConversationSubmit").click(() => {
            let name = $('#inputUsernameForConversation').val();
            let workspace;

            (async () => {
                workspace = await this.workspace_service.getChosenWorkspace();
            })();
            this.user_service.getUserByName(name).then(userTo => {
                if (userTo && userTo.message) {
                    alert('User not found');
                } else {
                    const entity = {
                        associatedUserId: userTo.id,
                        workspaceId: workspace.id,
                        openingUserId: this.logged_user.id,
                        showForOpener: true,
                        showForAssociated: true
                    };
                    this.conversation_serivce.create(entity).then(conv => {
                        this.chat_members.populateDirectMessages();
                        this.stomp_client.subscribeNewDirectMessage(conv.id)
                    });

                }
            })
        })
    }

    checkTopic(topic) {
        return topic === "" || topic === "null" ? "Enter channel topic here." : topic;
    }

    onAddChannelSubmit() {
        $("#addChannelSubmit").click(() => {
            const entity = {
                name: $('#exampleInputChannelName').val(),
                isPrivate: $('#exampleCheck1').is(':checked'),
                createdDate: this.getFormattedCreateDate(),
                ownerId: this.logged_user.id
            };

            this.channel_service.create(entity).then(chn => {
                if (chn.id !== undefined) {
                    sendChannel(chn);
                } else {
                    alert("A channel with the same name already exists.")
                }
            })
        });
    }

    onHideChannelModal() {
        $("#addChannelModal").on('hide.bs.modal', function () {
            $(":input", $('#addChannelModal')).val("");
            $('input[type=checkbox]').each(function () {
                this.checked = false;
            });
        });
    }


    getFormattedCreateDate() {
        const date = new Date();
        const options = {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        };
        return date.toLocaleString("ru", options).replace(/,/g, "");
    }
}