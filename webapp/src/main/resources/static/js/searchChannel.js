import {getAllChannels} from "./ajax/channelRestController.js";

//функция поиска совпадений вводимых символов
function findEl(el, array, value) {
    var coincidence = false;
    el.empty();//очищаем список совпадений
    for (var i = 0; i < array.length; i++) {
        if (array[i].match('^' + value)) {//проверяем каждый елемент на совпадение побуквенно
            el.children('li').each(function () {//проверяем есть ли совпавшие елементы среди выведенных
                if (array[i] === $(this).text()) {
                    coincidence = true;//если есть совпадения то true
                }
            });
            if (coincidence === false) {
                el.append('<li class="js-filter-address">' + array[i] + '</li>');//если нету совпадений то добавляем уникальное название в список
            }
        }
    }
}

var filterInput = $('#filter-address'),
    filterUl = $('.ul-addresses');
var channelNames = [];

$(document).ready(() => {
    showAllChannels();
});

const showAllChannels = () => {
    const channels = getAllChannels();
    $.each(channels, (i, item) => {
        channelNames.push(`${item.name}`);
})
};


//проверка при каждом вводе символа
filterInput.bind('input propertychange', function () {

    if ($(this).val() !== '') {
        filterUl.fadeIn(100);
        // findEl(filterUl, $(this).data('address'), $(this).val());
        findEl(filterUl, channelNames, $(this).val());
    } else {
        filterUl.fadeOut(100);
    }
});
//при клике на елемент выпадалки присваиваем значение в инпут и ставим триггер на его изменение
filterUl.on('click', '.js-filter-address', function (e) {
    $('#filter-address').val('');
    filterInput.val($(this).text());
    filterInput.trigger('change');
    filterUl.fadeOut(100);
});
