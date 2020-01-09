import {ModalSearcher} from "./ModalSearcher.js";
import {SearcherData} from "./SearcherData.js";

const modal_searcher = new ModalSearcher();
const searcher_data = new SearcherData();

$(document).ready(async () => {
    await searcher_data.setWorkspaceId();
    await searcher_data.setAvailableChannels();
    await searcher_data.setAvailableUsers();

    modal_searcher.setSearchData(searcher_data);
});

window.addEventListener('load', () => {
   modal_searcher.onSearchClick().onInputSelection().inputSearchBind();
});