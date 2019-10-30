
export const sendCode = (code) => {
    let result = null;
    $.ajax({
        type: 'post',
        url: "/api/create/confirmEmail",
        async: false,
        data: code,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const sendEmail = (email) => {
    let result = null;
    $.ajax({
        type: 'post',
        url: "/api/create/sendEmail",
        async: false,
        data: email,
        contentType: "application/json",
    }).done(function (data) {
        result = data;
    });
        return result;
};

export const takeWorkspaceName = (name) => {
    let result = null;
    $.ajax({
        type: 'post',
        url: "/api/create/workspaceName",
        async: false,
        data: name,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const takeChannelName = (name) => {
    let result = null;
    $.ajax({
        type: 'post',
        url: "/api/create/channelName",
        async: false,
        data: name,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const sendInvites = (emails) => {
    let result = null;
    $.ajax({
        type: 'post',
        url: "/api/create/invites",
        async: false,
        data: JSON.stringify(emails),
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const getChannelName = () => {
    let result = null;
    $.ajax({
        type: 'post',
        url: "/api/create/tada",
        async: false,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

