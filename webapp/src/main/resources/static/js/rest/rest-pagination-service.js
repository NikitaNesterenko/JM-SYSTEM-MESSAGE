export class RestPaginationService {
    constructor(url) {
        this.url
            = url;
    }

    getPage = async (numberPage, size, sort) => {
        const response = await fetch(`${this.url}?page=${numberPage}&size=${size}&sort=${sort}`);
        return response.json();
    };

    getAll = async () => {
        const response = await fetch(`${this.url}`);
        return response.json();
    };

    getById = async (id) => {
        const response = await fetch(`${this.url}/${id}`);
        return response.json();
    };

    create = async (entity) => {
        const response = await fetch(`${this.url}/create`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(entity)
        });
        return response.json();
    };

    update = async (entity) => {
        await fetch(`${this.url}/update`, {
            method: 'PUT',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(entity)
        });
    };

    deleteById = async (id) => {
        await fetch(`${this.url}/delete/${id}`, {
            method: 'DELETE'
        });
    };
}