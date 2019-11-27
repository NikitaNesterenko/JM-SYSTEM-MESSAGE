// side panel
export const close_right_panel = $('.p-flexpane_header__control__button').on('click', () => {
        $('.p-workspace').css('grid-template-areas', '"p-workspace__top_nav p-workspace__top_nav p-workspace__top_nav"\n' +
            '            "p-workspace__sidebar p-workspace__primary_view p-workspace__primary_view"\n' +
            '            "p-workspace__sidebar p-workspace_footer p-workspace_footer"');
        $('.p-workspace__secondary_view').css('display', 'none');
});

/*
* in your code:
* 1. import {open_right_panel} from "../right_slide_panel/right_panel.js";
* 2. $(your_selector).on('click', open_right_panel);
* */
export const open_right_panel = () => {
    $('.p-workspace').css('grid-template-areas', '"p-workspace__top_nav p-workspace__top_nav p-workspace__top_nav"\n' +
        '                          "p-workspace__sidebar p-workspace__primary_view p-workspace__secondary_view"\n' +
        '                         "p-workspace__sidebar p-workspace_footer p-workspace__secondary_view"');
    $('.p-workspace__secondary_view').css('display', 'grid');
};