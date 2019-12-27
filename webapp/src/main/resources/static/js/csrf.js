//Get token from cookies
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

const csrfToken = getCookie('XSRF-TOKEN');

//Set token to <meta/>
let link = document.createElement('meta');
link.setAttribute('name', '_csrf');
link.content = csrfToken;
document.getElementsByTagName('head')[0].appendChild(link);

//For Fetch requests
let headers = new Headers();

headers.append("Accept", "application/json, text/plain, */*");
headers.append("Content-Type", "application/json");
headers.append("X-XSRF-TOKEN", (!(csrfToken === null)) ? csrfToken : ('meta[name="_csrf"]').attr('content'));

//For AJAX requests
$(window).on("load", function () {
//    $(document).ajaxSend(function (e, xhr, options) {
//        xhr.setRequestHeader("X-XSRF-TOKEN", csrfToken);
//    });
    $.ajaxSetup({
        beforeSend: function (xhr) {
            xhr.setRequestHeader("X-XSRF-TOKEN", csrfToken)

        }
    });

});
