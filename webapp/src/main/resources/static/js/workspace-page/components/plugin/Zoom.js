import {PluginRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {MessageRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {MessageView} from "/js/workspace-page/components/messages/MessageView.js";

export class Zoom extends MessageView {

    constructor(logged_user) {
        super(logged_user);
        this.dialog.messageBox("#all-messages");
        this.plugin_service = new PluginRestPaginationService();
        this.message_service = new MessageRestPaginationService();
        this.stompClient = Stomp.over(new SockJS('/websocket'));
    }

    sendMessage() {
        this.plugin_service.createMeetings().then(
            response => {
                if (response.redirect_uri == null) {
                    const content = response.id + "|" + response.join_url + "|" + this.logged_user.displayName;
                    const entity = {
                        id: null,
                        channelId: channel_id,
                        botId: 2,
                        botNickName: "Zoom",
                        userName: this.logged_user.displayName,
                        content: content,
                        dateCreate: convert_date_to_format_Json(new Date(response.created_at)),
                    };
                    this.message_service.create(entity).then(
                        msg => {
                            msg['inputMassage'] = msg.content;
                            this.stompClient.send("/app/message", {}, JSON.stringify(msg));
                            this.createMessage(this.zoomMeeting(msg));
                        }
                    );
                } else {
                    this.authorizeZoom(response.redirect_uri);

                }
            }
        );
    }

    createMessage(message) {
        const msg = {
            id: message.id,
            userId: null,
            botId: message.botId,
            dateCreate: convert_date_to_format_Json(new Date()),
            userAvatarUrl: "https://slack-files2.s3-us-west-2.amazonaws.com/avatars/2017-09-21/245295805989_06e77af1bfb8e3c81d4d_72.png",
            content: message.content
        };
        this.dialog.setUser(0, "Zoom")
            .container(msg)
            .setAvatar()
            .setMessageContentHeader()
            .setPluginContent();
        this.addAppBadge(msg);
    }

    addAppBadge(msg) {
        $(`#message_${msg.id}_user_${msg.botId}_content_header`)
            .append(`
                <span class="badge badge-secondary" 
                        style="background-color: rgba(var(--sk_foreground_low,29,28,29),.13); color: rgba(var(--sk_foreground_max,29,28,29),.7);">
                        APP
                </span>`);
    }

    authorizeZoom(link) {
        this.createMessage(
            {
                id: 0,
                userId: null,
                botId: 0,
                dateCreate: convert_date_to_format_Json(new Date()),
                userAvatarUrl: "https://slack-files2.s3-us-west-2.amazonaws.com/avatars/2017-09-21/245295805989_06e77af1bfb8e3c81d4d_72.png",
                content:
                    `
            <div class="c-message__content_body">
                    <div class="my-2 pt-2">Please authorize JM-System to access your Zoom information by clicking the link below:</div>
                    <span>
                        <img src="https://a.slack-edge.com/production-standard-emoji-assets/10.2/apple-medium/1f449@2x.png" style="width: 22px; height: 22px;">
                    </span>
                    <b><a target="_blank" class="" href="${link}" rel="noopener noreferrer">Authorize Zoom</a></b>
                    <div class="mt-2">This link is valid for 30 minutes and can only be accessed once.</div>
            </div>
         `});
        $('#message_0_user_0_content').prepend(`
                        <div class="c-message__content_header mb-3 pb-1">
                            <span class="c-message__gutter--feature_sonic_inputs">
                                <img src="https://image0.flaticon.com/icons/png/128/61/61916.png" alt="" style="width: 20px;height: 20px;">
                            </span>
                            <span class="c-message__sender ml-1" >
                                    Only visible to you
                            </span>
                        </div>
                    `);
        this.dialog.root.css("background", "#e6e6e6");
    }

    zoomMeeting(message) {
        const [id, url, displayName] = message.content.split('|');
        return {
            id: 0,
            userId: null,
            botId: 0,
            dateCreate: convert_date_to_format_Json(new Date()),
            userAvatarUrl: "https://slack-files2.s3-us-west-2.amazonaws.com/avatars/2017-09-21/245295805989_06e77af1bfb8e3c81d4d_72.png",
            content:
                `
            <div class="c-message__body" style="color: rgba(var(--sk_foreground_max_solid,97,96,97),1)">
                Call
              <button type="button" class="dropdown-toggle dropdown-toggle-split bg-transparent border-0 p-0 m-0" href="#"  id="dropdownMenuLink" >
              </button>   
            </div>
            <div class="c-message__content_body mb-3">
                <div class="card" style="max-width: 450px; border-radius: 8px;">
                    <div class="card-header pl-3 bg-transparent">
                        <div class="row">
                            <div class="col-auto py-0">
                                <img src="https://slack-files2.s3-us-west-2.amazonaws.com/avatars/2017-09-21/245295805989_06e77af1bfb8e3c81d4d_36.png" class="rounded float-left">
                            </div>
                            <div class="col-auto py-0 pl-0">
                                <b class="mb-1">Zoom meeting started by ${displayName}</b>
                                <p class="mt-2 mb-0">Started at ${message.dateCreate}</p>
                            </div>
                        </div>
                    </div>
                    <div class="card-body pl-3 bg-light py-3">
                        <span class="" style="color: rgba(var(--sk_foreground_max,29,28,29),.7)">Meeting ID: ${id.match(/.{1,3}/g).join("-")}</span>
                    </div>
                    <div class="card-footer pl-3 bg-light">
                        <div class="row">
                            <div class="col-auto my-auto">
                                <span class="" style="color: rgba(var(--sk_foreground_max,29,28,29),.7)">Waiting for people to join</span> 
                            </div>
                            <div class="col">
                                <a href="${url}" rel="noopener noreferrer" target="_blank" class="btn btn-success float-right" type="button" aria-label="Enter to join call.">Join</a> 
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `};
    }
}