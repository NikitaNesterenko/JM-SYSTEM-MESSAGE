import { ownFetch } from './fetch-service.js';
export class RestPaginationService {
    constructor(url) {
        this.url
            = url;
    }

    getPage = async (numberPage, size, sort) => {
        const response = await ownFetch.get(`${this.url}?page=${numberPage}&size=${size}&sort=${sort}`);
        return response.json();
    };

    getAll = async () => {
        const response = await ownFetch.get(`${this.url}`);
        return response.json();
    };

    getById = async (id) => {
        const response = await ownFetch.get(`${this.url}/${id}`);
        return response.json();
    };

    getByName = async (chanelName) => {
        const response = await ownFetch.get(`${this.url}/name/${chanelName}`);
        return response.json();
    }

    create = async (entity) => {

        const response = await ownFetch.post(`${this.url}/create`,{
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(entity)
        });
        return response.json();
    };

    update = async (entity) => {
        await ownFetch.put(`${this.url}/update`, {
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(entity)
        });
    };

    deleteById = async (id) => {
        await ownFetch.delete(`${this.url}/delete/${id}`, {

        });
    };
}