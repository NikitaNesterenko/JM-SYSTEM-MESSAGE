export const ownFetch = {
    get: (url, opts) => {
        return fetch(url, {
            ...opts,
            method: 'GET',
        });
    },
    post: (url, opts) => {
        return fetch(url, {
            ...opts,
            method: 'POST',
        });
    },
    put: (url, opts) => {
        return fetch(url, {
            ...opts,
            method: 'PUT',
        });
    },
    delete: (url, opts) => {
        return fetch(url, {
            ...opts,
            method: 'DELETE',
        });
    },
};