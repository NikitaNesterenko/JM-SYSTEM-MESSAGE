import {close_right_panel, open_right_panel} from "../../right_slide_panel/right_panel";

class Toggle_right_menu {
    let toggle_right_menu = () => {
        if (is_open) {
            close_right_panel();
            is_open = false;
        } else {
            open_right_panel();
            populateRightPane();
            is_open = true;
        }
    };
}