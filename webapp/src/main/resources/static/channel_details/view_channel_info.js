import {close_right_panel, open_right_panel} from "../right_slide_panel/right_panel.js";

// toggle right panel
let is_open;
$(document).on('load', () => is_open = false);

let toggle_right_menu = () => {
    if (is_open) {
        close_right_panel();
        is_open = false;
    } else {
        open_right_panel();
        get_channel_info_panel();
        is_open = true;
    }
};

$('.p-classic_nav__model__button__details').on('click', () => {
    toggle_right_menu();
});

const get_channel_info_panel = () => {
    $('.p-flexpane__inside_body-scrollbar__child').append(`<div class="p-flexpane__inside_body__details_section">
                                                    <button class="p-channel_details_section__header">
                                                    <span class="p-channel_details_section__title">
                                                        <i class="p-channel_details_section__icon">ⓘ</i>
                                                        <span class="p-channel_details_section__title-content">Channel Details</span>
                                                    </span>
                                                        <i class="p-channel_details_section__caret_icon">►</i>
                                                    </button>
                                                </div>
                                                <div class="p-channel_details__highlights_section">
                                                    <button class="p-channel_details_section__header">
                                                    <span class="p-channel_details_section__title">
                                                        <i class="p-channel_details_section__icon p-channel_details_section__highlights__icon">✨</i>
                                                        <span class="p-channel_details_section__title-content">
                                                            Highlights
                                                        </span>
                                                    </span>
                                                        <i class="p-channel_details_section__caret_icon">►</i>
                                                    </button>
                                                </div>
                                                <div class="p-channel_details__pinned_items_section">
                                                    <button class="p-channel_details_section__header">
                                                    <span class="p-channel_details_section__title">
                                                        <i class="p-channel_details_section__icon p-channel_details_section__pinned_items__icon">📌</i>
                                                        <span class="p-channel_details_section__title-content">
                                                            - Pinned Item
                                                        </span>
                                                    </span>
                                                        <i class="p-channel_details_section__caret_icon">►</i>
                                                    </button>
                                                </div>
                                                <div class="p-channel_details__members_list_section">
                                                    <button class="p-channel_details_section__header">
                                                    <span class="p-channel_details_section__title">
                                                        <i class="p-channel_details_section__icon p-channel_details_section__members_list__icon">👨</i>
                                                        <span class="p-channel_details_section__title-content">
                                                            - Members
                                                        </span>
                                                    </span>
                                                        <i class="p-channel_details_section__caret_icon">►</i>
                                                    </button>
                                                </div>
                                                <div class="p-channel_details__apps_section">
                                                    <button class="p-channel_details_section__header">
                                                    <span class="p-channel_details_section__title">
                                                        <i class="p-channel_details_section__icon p-channel_details_section__apps__icon">💻</i>
                                                        <span class="p-channel_details_section__title-content">
                                                            Apps
                                                        </span>
                                                    </span>
                                                        <i class="p-channel_details_section__caret_icon">►</i>
                                                    </button>
                                                </div>
                                                <div class="p-channel_details__shared_files_section">
                                                    <button class="p-channel_details_section__header">
                                                    <span class="p-channel_details_section__title">
                                                        <i class="p-channel_details_section__icon p-channel_details_section__shared_files__icon">⌨</i>
                                                        <span class="p-channel_details_section__title-content">
                                                            Shared Files
                                                        </span>
                                                    </span>
                                                        <i class="p-channel_details_section__caret_icon">►</i>
                                                    </button>
                                                </div>
                                                <div class="p-channel_details__notification_prefs_section">
                                                    <button class="p-channel_details_section__header">
                                                    <span class="p-channel_details_section__title">
                                                        <i class="p-channel_details_section__icon p-channel_details_section__notification_prefs__icon">🔔</i>
                                                        <span class="p-channel_details_section__title-content">
                                                            Notification Preferences
                                                        </span>
                                                    </span>
                                                        <i class="p-channel_details_section__caret_icon">►</i>
                                                    </button>
                                                </div>`);
};