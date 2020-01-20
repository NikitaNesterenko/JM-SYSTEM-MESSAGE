export class ChannelDetailsItem {
    getItem() {
        return `
            <div class="p-flexpane__inside_body__details_section">
                <button class="p-channel_details_section__header">
                    <span class="p-channel_details_section__title">
                        <i class="p-channel_details_section__icon">ⓘ</i>
                        <span class="p-channel_details_section__title-content">Channel Details</span>
                    </span>
                    <i class="p-channel_details_section__caret_icon">►</i>
                </button>
            </div>
        `;
    }
}
