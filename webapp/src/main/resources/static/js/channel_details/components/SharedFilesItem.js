export class SharedFilesItem {
    getItem() {
        return `
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
        `;

    }
}
