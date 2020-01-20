export class Open_right_panel {
    open_right_panel(){
        $('.p-workspace').css('grid-template-areas', '"p-workspace__top_nav p-workspace__top_nav p-workspace__top_nav"\n' +
            '                          "p-workspace__sidebar p-workspace__primary_view p-workspace__secondary_view"\n' +
            '                         "p-workspace__sidebar p-workspace_footer p-workspace__secondary_view"');
        $('.p-workspace__secondary_view').css('display', 'grid');
    }
}