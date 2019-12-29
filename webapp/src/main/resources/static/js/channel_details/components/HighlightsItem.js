export class HighlightsItem {
    getItem() {
        return `
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
        `;
    }
}
