export const getWorkspaceById = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/restapi/workspaces/workspace/" + id
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const createWorkspace = (workspace) => {
    let result = null;
    const jsonWorkspace = JSON.stringify(workspace);
    $.ajax({
        type: 'post',
        url: "/restapi/workspaces/create",
        async: false,
        data: jsonWorkspace,
        contentType: "application/json"
    }).done(function (data) {
        result = data;
    });
    return result;
};

export const updateWorkspace = (workspace) => {
    const jsonWorkspace = JSON.stringify(workspace);
    $.ajax({
        type: 'put',
        url: "/restapi/workspaces/update",
        async: false,
        data: jsonWorkspace,
        contentType: "application/json"
    });
};

export const deleteWorkspace = (id) => {
    $.ajax({
        type: 'delete',
        url: "/restapi/workspaces/delete/" + id,
        async: false
    });
};

export const getAllWorkspaces = () => {
    let workspaces = null;
    $.ajax({
        url: '/restapi/workspaces/',
        async: false,
        type: 'get',
        success: function (response) {
            workspaces = response;
        }
    });
    return workspaces;
};

