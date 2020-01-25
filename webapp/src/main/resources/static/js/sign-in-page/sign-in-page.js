import {WorkspaceSignIn} from "./components/WorkspaceSignIn.js";

const form = new WorkspaceSignIn();

window.addEventListener("load", function () {
    form.onSubmit();
});
