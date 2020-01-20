import {WorkspaceNameHandle} from "./components/workspaceNameHandler.js";


const handler = new WorkspaceNameHandle();
$(document).ready(() => handler.documentReady());