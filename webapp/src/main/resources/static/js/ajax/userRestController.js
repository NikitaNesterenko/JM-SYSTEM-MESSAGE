export const getUsers = () => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/restapi/users"
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
        url: "/restapi/users/" + id
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
        url: "/restapi/users/create",
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
        url: "/restapi/users/update",
        async: false,
        data: jsonUser,
        contentType: "application/json"
    });
};

export const deleteUser = (id) => {
    $.ajax({
        type: 'delete',
        async: false,
        url: "/restapi/users/delete/" + id
    });
};


