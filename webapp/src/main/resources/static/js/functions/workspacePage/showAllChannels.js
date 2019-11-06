export const showAllChannels = () => {
    const channels = channel_service.getAll();
    $.each(channels, (i, item) => {
        $('#id-channel_sidebar__channels__list').append(`<div class="p-channel_sidebar__channel">
                                                    <button class="p-channel_sidebar__name_button">
                                                        <i class="p-channel_sidebar__channel_icon_prefix">#</i>
                                                        <span class="p-channel_sidebar__name-3">${item.name}</span>
                                                    </button>
                                                  </div>`);
    })
};