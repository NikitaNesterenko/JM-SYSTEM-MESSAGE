import {CreateWorkspacePaginationService} from './rest/entities-rest-pagination.js'

const  sendEmail_service = new CreateWorkspacePaginationService();

$(document).ready(function() {
   $("#button-email").click(function(){
      let email = $('#target-email').val();
      // sendEmail(email);
      const result = sendEmail_service.sendEmail(email);
      result.then(email => { //После того как ответ будет получен, начнется выполнение этого блока
         window.location.href = "/confirmemail";
         return false;
      });
   });
});