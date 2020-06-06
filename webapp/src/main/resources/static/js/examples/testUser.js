import { ownFetch } from '/static/js/rest/fetch-service.js';
$(document).ready(function () {
    let user;
    $('#getUser').on('click', function () {
        alert("GET");
        $.ajax({
            type: 'ajax',
            method: 'GET',
            url: "/rest/api/users/1",
            async: false,
            dataType: 'json',
            success: function (data) {
                user = data;
            },
            error: function () {

            }
        })
    });
    //   user.id = 100;

    $('#setUser').on('click', function () {
        alert("POST");

        ownFetch.post('/rest/api/users/create', {
            headers: headers,
            body: JSON.stringify(user)
        })
            .then(function (response) {
                return response.json();
            })
            .then(function (result) {
                alert(result);
            })
            .catch(function (error) {
                console.log('Request failed', error);
            });

    })

});