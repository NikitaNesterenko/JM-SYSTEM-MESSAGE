import {
    ConversationRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService,
    DirectMessagesRestController
} from "/js/rest/entities-rest-pagination.js";

export class ActiveChatMembers {

    constructor() {
        this.user_service = new UserRestPaginationService();
        this.conversation_service = new ConversationRestPaginationService();
        this.workspace_service = new WorkspaceRestPaginationService();
        this.directMessage_service = new DirectMessagesRestController();
    }

    async populateDirectMessages() {
        const principal = await this.user_service.getLoggedUser();
        const conversations = await this.conversation_service.getAllConversationsByUserId(principal.id);
        const workspace_id = await this.workspace_service.getChosenWorkspace();

        const direct_messages_container = $("#direct-messages__container_id");
        direct_messages_container.empty();

        conversations.forEach((conversation, i) => {
            if (conversation.workspace.id === workspace_id.id) {
                const conversation_queue_context_container = $('<div class="p-channel_sidebar__channel" ' +
                    'style="height: min-content; width: 100%;"></div>');
                conversation_queue_context_container.className = "p-channel_sidebar__channel";
                if (conversation.openingUser.id === principal.id) {
                    conversation_queue_context_container.append(this.messageChat(conversation.associatedUser, conversation.id));
                } else {
                    conversation_queue_context_container.append(this.messageChat(conversation.openingUser, conversation.id));
                }
                direct_messages_container.append(conversation_queue_context_container);
                //const unreadMessages = this.directMessage_service.getUnreadDMessagesInConversationForUser(conversation.id, principal.id)
                //проверка, есть ли непрочтенные сообщение для данной беседы у пользователя
                this.directMessage_service.getUnreadDMessagesInConversationForUser(conversation.id, principal.id).then(messages => {
                    if (messages.length > 0) {
                        this.enableDirectHasUnreadMessage(conversation.id)
                    }
                })
            }
        });
    }

    messageChat(user, conversationId) {
        return `
            <button class="p-channel_sidebar__name_button" data-user_id="${user.id}" conv_id="${conversationId}">
                <i class="p-channel_sidebar__channel_icon_circle pb-0" data-user_id="${user.id}">${user.online == 1 ? "●" : "○"}</i>
                <span class="p-channel_sidebar__name-3" data-user_id="${user.id}">
                    <span data-user_id="${user.id}" conv_id="${conversationId}">${user.name}</span>
                </span>
            </button>
            <button class="p-channel_sidebar__close cross">
                <i id="deleteDmButton" data-conversationId="${conversationId}" class="p-channel_sidebar__close__icon">✖</i>
            </button>
        `;
    }

    enableDirectHasUnreadMessage = (convId) => {
        document.querySelector(`span[conv_id='${convId}']`).classList.add("font-weight-bold");
        document.querySelector(`span[conv_id='${convId}']`).classList.add("text-white");
    };
}