import {DocumentReady} from "./components/documentReady.js"
import {AddEventListener} from "./components/addEventListener.js"

let users = [];// all users from the database; updated every time after editing and updating any user

let addEventListener = new AddEventListener();
window.addEventListener('load', addEventListener.addEventListener() );

let documentReady = new DocumentReady();
$(document).ready(documentReady.documentReady());

