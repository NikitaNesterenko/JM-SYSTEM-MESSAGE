export const getWorkspaceById = (id) => {
    let result = null;
    $.ajax({
        type: 'get',
        async: false,
        url: "/rest/api/workspaces/workspace/" + id
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
        url: "/rest/api/workspaces/create",
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
        url: "/rest/api/workspaces/update",
        async: false,
        data: jsonWorkspace,
        contentType: "application/json"
    });
};

export const deleteWorkspace = (id) => {
    $.ajax({
        type: 'delete',
        url: "/rest/api/workspaces/delete/" + id,
        async: false
    });
};

export const getAllWorkspaces = () => {
    let workspaces = null;
    $.ajax({
        url: '/rest/api/workspaces/',
        async: false,
        type: 'get',
        success: function (response) {
            workspaces = response;
        }
    });
    return workspaces;
};

