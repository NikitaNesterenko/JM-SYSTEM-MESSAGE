import {DocumentReady} from "./components/documentReady.js";
import {Loadhandler} from "./components/loadhandler.js";

const handler = new Loadhandler();
const documentReady = new DocumentReady();

window.addEventListener('load', handler.handller);

$(document).ready(documentReady.documentReady());