import {findEl} from "../filter-channel.js"
import {UserRestPaginationService} from '../rest/entities-rest-pagination.js'

const user_service = new UserRestPaginationService();
const filterInput = $('#filter-channel'), filterUl = $('.ul-channel');
let channelNames = [];

$(document).ready(() => {
    showAllChannels();
});

const showAllChannels = () => {
    const channels_promise = user_service.getAll();
    channels_promise.then(channels => {         //После того как Чаннелсы будут получены, начнется выполнение этого блока
        $.each(channels, (i, item) => {
            channelNames.push(`${item.name}`);
        })
    });
};


//проверка при каждом вводе символа
filterInput.on('input propertychange', function () {

    if ($(this).val() !== '') {
        filterUl.fadeIn(100);
        findEl(filterUl, channelNames, $(this).val());
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
