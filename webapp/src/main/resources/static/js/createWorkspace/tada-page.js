import {CreateWorkspacePaginationService} from './rest/entities-rest-pagination.js'

const  getChannelName_service = new CreateWorkspacePaginationService();

// let respons = getChannelName();
let respons = getChannelName_service.getChannelName();
respons.then(responce => { //После того как ответ будет получен, начнется выполнение этого блока
    let textP = $('#workspace_name_label');
    textP.text("Tada! Meet your team’s first channel: #" + respons);
});


