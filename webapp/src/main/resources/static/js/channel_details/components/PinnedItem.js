export class PinnedItem {
    getItem() {
        return `
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
        `;
    }
}
