export class DeleteTablesDataForWorkspacesPage {
    constructor() {
    }

    deleteTablesDataForWorkspacesPage(){
        let to_delete_elements1 = document.getElementsByClassName('update_delete');
        while (to_delete_elements1.length > 0) {
            to_delete_elements1[0].parentNode.removeChild(to_delete_elements1[0]);
        }

        let to_delete_elements2 = document.getElementsByClassName('create');
        while (to_delete_elements2.length > 0) {
            to_delete_elements2[0].parentNode.removeChild(to_delete_elements2[0]);
        }

        let to_delete_elements3 = document.getElementsByClassName('users_for_workspace');
        while (to_delete_elements3.length > 0) {
            to_delete_elements3[0].parentNode.removeChild(to_delete_elements3[0]);
        }

        let to_delete_elements4 = document.getElementsByClassName('user_to_add_in_workspace');
        while (to_delete_elements4.length > 0) {
            to_delete_elements4[0].parentNode.removeChild(to_delete_elements4[0]);
        }
    }
}