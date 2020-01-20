import {MenuChatBox} from "./components/MenuChatBox.js";

const m = new MenuChatBox();
$('#form_message').keyup(() => m.findActions());