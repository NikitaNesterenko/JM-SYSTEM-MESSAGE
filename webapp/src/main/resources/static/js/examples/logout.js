$(document).ready(function () {
    let inputCsrf = document.createElement('input');
    inputCsrf.setAttribute("type", "hidden");
    inputCsrf.setAttribute("name", "_csrf");
    inputCsrf.setAttribute("value", csrfToken);
    $("#logoutForm").append(inputCsrf);

});
