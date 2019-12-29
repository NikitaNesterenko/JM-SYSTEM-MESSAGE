import {attachMemberListBtnClickHandler} from "/js/member-list/member-list.js";

export class MembersItem {

    getItem() {
        attachMemberListBtnClickHandler();

        return `
            <div class="p-channel_details__members_list_section">
                <button class="p-channel_details_section__header" id="memberListBtn">
                    <span class="p-channel_details_section__title">
                        <i class="p-channel_details_section__icon p-channel_details_section__members_list__icon">👨</i>
                        <span class="p-channel_details_section__title-content">
                            - Members
                        </span>
                    </span>
                    <i class="p-channel_details_section__caret_icon" id="memberListCaretSymbol">►</i>
                </button>
                <div id="memberListPlaceholder">
                    <!-- Member List will be placed here -->
                </div>
            </div>
        `;
    }
}
