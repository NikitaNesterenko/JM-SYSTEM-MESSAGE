import {sendEmail} from "../ajax/createWorkspaceRestController.js";

$(document).ready(function() {
   $("#button-email").click(function(){
      let email = $('#target-email').val();
      sendEmail(email);
      window.location.href = "/confirmemail";
      return false;
   });
});