export let is_open_thread = false;

export const close_right_thread_panel = () => {
    $('.p-workspace__secondary_view').css('display', 'none');

    is_open_thread = false;
};


export const open_right_thread_panel = () => {
    $('.p-workspace').css('grid-template-areas', '"p-workspace__top_nav p-workspace__top_nav p-workspace__top_nav"\n' +
        '                          "p-workspace__sidebar p-workspace__primary_view p-workspace__secondary_view"\n' +
        '                         "p-workspace__sidebar p-workspace__footer p-workspace__secondary_view"');
    $('.p-workspace__secondary_view').css('display', 'grid');

    is_open_thread = true;
};

$('.p-flexpane_header__control__button').on('click', close_right_thread_panel);