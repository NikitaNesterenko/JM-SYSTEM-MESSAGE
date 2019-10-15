import * as jQuery from '/js/jquery-3.4.1.min.js';

export const getUsers = () => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/api/users/"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const getUser = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/api/users/" + id
    }).done(function (data) {
        result = data;
    });
    return result;
};

export function createUser(user) {
    const jsonUser = JSON.stringify(user);
    $.ajax({
        type: 'post',
        url: "/api/users/",
        data: jsonUser,
        contentType: "application/json"
    });
}

export function updateUser(user) {
    const jsonUser = JSON.stringify(user);
    $.ajax({
        type: 'put',
        url: "/api/users/",
        data: jsonUser,
        contentType: "application/json"
    });
}

export const deleteUser = (id) => {
    $.ajax({
        type: 'delete',
        url: "/api/users/" + id
    });
};


