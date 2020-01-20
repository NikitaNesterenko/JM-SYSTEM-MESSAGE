import {WorkspaceSelectBox} from "./components/WorkspaceSelectBox.js";

const selectBox = new WorkspaceSelectBox();

$(document).ready(() => { selectBox.workspaceOptions().watchChanges() });