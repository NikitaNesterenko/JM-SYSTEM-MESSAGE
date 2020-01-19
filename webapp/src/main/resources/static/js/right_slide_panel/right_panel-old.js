import {Open_right_panel} from "./components/open_right_panel.js"
import {Close_right_panel} from "./components/close_right_panel.js"

// side panel

/*
* in your code:
* 1. import {open_right_panel, close_right_panel} from "../right_slide_panel/right_panel-old.js";
* 2. $(your_selector).on('click', open_right_panel);
* */
const op_right_panel = new Open_right_panel();
const cl_right_panel = new Close_right_panel();
export const close_right_panel = () => { op_right_panel.open_right_panel()};



export const open_right_panel = () => { cl_right_panel.close_right_panel()};


$('.p-flexpane_header__control__button').on('click', close_right_panel);