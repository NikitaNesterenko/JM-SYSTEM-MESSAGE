import {
    ConversationRestPaginationService,
    UserRestPaginationService,
    WorkspaceRestPaginationService
} from "../../../rest/entities-rest-pagination.js";

export class ActiveChatMembers {

    constructor() {
        this.user_service = new UserRestPaginationService();
        this.conversation_service = new ConversationRestPaginationService();
        this.workspace_service = new WorkspaceRestPaginationService();
    }

    async populateDirectMessages() {
        const principal = await this.user_service.getLoggedUser();
        const conversations = await this.conversation_service.getAllConversationsByUserId(principal.id);
        const workspace_id = await this.workspace_service.getChoosedWorkspace();

        const direct_messages_container = $("#direct-messages__container_id");
        direct_messages_container.empty();

        conversations.forEach((conversation, i) => {
            if (conversation.workspace.id === workspace_id.id) {
                const conversation_queue_context_container = $('<div></div>');
                conversation_queue_context_container.className = "p-channel_sidebar__close_container";
                if (conversation.openingUser.id === principal.id) {
                    conversation_queue_context_container.append(this.messageChat(conversation.associatedUser));
                } else {
                    conversation_queue_context_container.append(this.messageChat(conversation.openingUser));
                }
                direct_messages_container.append(conversation_queue_context_container);
            }
        });
    }

    messageChat(user) {
        return `
            <button class="p-channel_sidebar__name_button" data-user_id="${user.id}">
                <i class="p-channel_sidebar__channel_icon_circle pb-0" data-user_id="${user.id}">●</i>
                <span class="p-channel_sidebar__name-3" data-user_id="${user.id}">
                    <span data-user_id="${user.id}">${user.name}</span>
                </span>
            </button>
            <button class="p-channel_sidebar__close">
                <i class="p-channel_sidebar__close__icon">✖</i>
            </button>
        `;
    }
}