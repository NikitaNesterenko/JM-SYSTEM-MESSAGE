export const getUsers = () => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/rest/api/users"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const getAllUsersInThisChannel = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/restapi/users/channel/" + id
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
        url: "/rest/api/users/" + id
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const createUser = (user) => {
    let result = null;
    const jsonUser = JSON.stringify(user);
    $.ajax({
        type: 'post',
        url: "/rest/api/users/create",
        async: false,
        data: jsonUser,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const updateUser = (user) => {
    const jsonUser = JSON.stringify(user);
    $.ajax({
        type: 'put',
        url: "/rest/api/users/update",
        async: false,
        data: jsonUser,
        contentType: "application/json"
    });
};

export const getAllUsersInThisChannel = (id) => {
    let result ;
    $.ajax({
        type: 'get',
        async: false,
        url: "/rest/api/users/channel/" + id
    }).done(function (data) {
        result = data;
    });
    return result;

};


export const deleteUser = (id) => {
    $.ajax({
        type: 'delete',
        async: false,
        url: "/rest/api/users/delete/" + id
    });
};


