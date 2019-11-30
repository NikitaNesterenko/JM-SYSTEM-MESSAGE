import {UserRestPaginationService} from './rest/entities-rest-pagination.js'

const user_service = new UserRestPaginationService();
const modal = document.getElementById('associatedUserList');

$("#form_message_input").on('input', function () {
    let text = document.getElementById('form_message_input').value;
    if (text.length === 1 && text === "@"){
        modal.style.display = "block";
        showAllUsers()
    }
});

const showAllUsers = () => {
    const allUsers = user_service.getAll();
    allUsers.then(response => {           //После того как Юзеры будут получены, начнется выполнение этого блока
        $.each(response, (i, item) => {
            $('#associatedUserListSelect')
                .append(
                    `<option id="atUserSelectOption" class="atUserSelectOption" value="${item.id}">${item.name}</option>`
                );
        });
    });
};