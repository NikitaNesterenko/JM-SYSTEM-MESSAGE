export class NotificationPreferencesItem {
    getItem() {
        return `
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
            </div>
        `;
    }
}
