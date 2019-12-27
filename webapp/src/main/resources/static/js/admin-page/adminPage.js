import {DocumentReady} from "./components/documentReady.js"
import {AddEventListener} from "./components/addEventListener.js"

let addEventListener = new AddEventListener();
window.addEventListener('load', addEventListener.addEventListener() );

let documentReady = new DocumentReady();
$(document).ready(documentReady.documentReady());

