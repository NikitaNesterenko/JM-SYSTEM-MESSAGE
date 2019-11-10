import {findEl} from "./filter-channel.js"
import {UserRestPaginationService} from './rest/entities-rest-pagination.js'


const filterInput = $('#filter-channel'), filterUl = $('.ul-channel');
const user_service = new UserRestPaginationService();
let userNames = [];

$(document).ready(() => {
    showAllUsersOnThisChannel();
});

const showAllUsersOnThisChannel = () => {
    const users_promise = user_service.getAllUsersInThisChannel();
    users_promise.then(users => {         //После того как Юзеры будут получены, начнется выполнение этого блока
        $.each(users, (i, item) => {
            userNames.push(`${item.name}`);
        })
    });
};


//проверка при каждом вводе символа
filterInput.on('input propertychange', function () {

    if ($(this).val() !== '') {
        filterUl.fadeIn(100);
        findEl(filterUl, userNames, $(this).val());
    } else {
        filterUl.fadeOut(100);
    }
});

//при клике на елемент выпадалки присваиваем значение в инпут и ставим триггер на его изменение
filterUl.on('click', '.js-filter-channel', function (e) {
    $('#filter-address').val('');
    filterInput.val($(this).text());
    filterInput.trigger('change');
    filterUl.fadeOut(100);
});


