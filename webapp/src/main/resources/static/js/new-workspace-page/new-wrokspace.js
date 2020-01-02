import {NewWorkspaceEventHandler} from "./components/NewWorkspaceEventHandler.js";

const handler = new NewWorkspaceEventHandler();
$(document).ready(() => handler.documentReady());