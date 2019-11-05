function getCookie(name) {
    if (!document.cookie) {
        return null;
    }

    const xsrfCookies = document.cookie.split(';')
        .map(c => c.trim())
        .filter(c => c.startsWith(name + '='));

    if (xsrfCookies.length === 0) {
        return null;
    }

    return decodeURIComponent(xsrfCookies[0].split('=')[1]);
}

const csrfToken = getCookie('CSRF-TOKEN');

let CSRFtoken = "";
let CSRFheader = "";


let tokenString = "<meta name='_csrf' content='"+ CSRFtoken + "'/><meta name='_csrf_header' content='"+ CSRFheader +"'/>";
let link = document.createElement('meta');
link.setAttribute('name', '_csrf');
link.content = csrfToken;
document.getElementsByTagName('head')[0].appendChild(link);


$(document).ready(function (data) {
    CSRFheader = data.name;
    CSRFtoken = data.value;
    let tokenString = "<meta name='_csrf' content='"+ csrfToken + "'/><meta name='_csrf_header' content='"+ CSRFheader +"'/>";
    $("head").append(tokenString);
    document.getElementsByTagName('head')[0].appendChild(tokenString);
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader("X-XSRF-TOKEN", csrfToken);
    });
});

