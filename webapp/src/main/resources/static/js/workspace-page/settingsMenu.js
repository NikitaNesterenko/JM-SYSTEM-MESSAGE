import {MenuSettingsModal} from "./components/MenuSettingsModal.js";

const menu_settings_modal = new MenuSettingsModal();

window.addEventListener('load', function () {
    menu_settings_modal.buildEvents();
});