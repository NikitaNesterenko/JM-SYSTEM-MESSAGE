export const showAllUsers = () => {
    const users_promise = user_service.getAll();
    users_promise.then(users => {           //После того как Юзеры будут получены, начнется выполнение этого блока
        $.each(users, (i, item) => {
            $('#associatedUserListSelect')
                .append(
                    `<option id="atUserSelectOption" class="atUserSelectOption" value="${item.id}">${item.name}</option>`
                );
        });
    });
};