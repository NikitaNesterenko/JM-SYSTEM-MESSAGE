import {findEl} from "../filter-channel.js"
import {getAllUsersInThisChannel} from "../ajax/userRestController.js";


const filterInput = $('#filter-channel'), filterUl = $('.ul-channel');
let channelNames = [];

$(document).ready(() => {
    showAllChannels();
});

const showAllChannels = () => {
    const channels = getAllUsersInThisChannel(1);
    alert(getAllUsersInThisChannel(1));
    $.each(channels, (i, item) => {
        alert(`${item.name}`)
        channelNames.push(`${item.name}`);
    })
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


