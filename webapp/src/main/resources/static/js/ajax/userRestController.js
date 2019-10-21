export const getUsers = () => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/restapi/users/users/"
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
        url: "/restapi/users/user/" + id
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const createUser = (user) => {
    const jsonUser = JSON.stringify(user);
    $.ajax({
        type: 'post',
        url: "/restapi/users/create",
        data: jsonUser,
        contentType: "application/json"
    });
};

export const updateUser = (user) => {
    const jsonUser = JSON.stringify(user);
    $.ajax({
        type: 'put',
        url: "/restapi/users/update",
        data: jsonUser,
        contentType: "application/json"
    });
};

export const deleteUser = (id) => {
    $.ajax({
        type: 'delete',
        url: "/restapi/users/delete/" + id
    });
};


