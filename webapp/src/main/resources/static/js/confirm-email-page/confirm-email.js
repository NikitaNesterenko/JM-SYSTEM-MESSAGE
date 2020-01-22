import {TwoFactorAuthentication} from "./components/TwoFactorAuthentication.js";

const widget = new TwoFactorAuthentication({root: "input"});
widget.setInputEvents();