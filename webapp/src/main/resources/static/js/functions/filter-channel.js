//функция поиска совпадений вводимых символов
export function findEl(el, array, value) {
    let coincidence = false;
    el.empty();//очищаем список совпадений
    for (let i = 0; i < array.length; i++) {
        if (array[i].match('^' + value)) {//проверяем каждый елемент на совпадение побуквенно
            el.children('li').each(function () {//проверяем есть ли совпавшие елементы среди выведенных
                if (array[i] === $(this).text()) {
                    coincidence = true;//если есть совпадения то true
                }
            });
            if (coincidence === false) {
                el.append('<li class="js-filter-channel">' + array[i] + '</li>');//если нету совпадений то добавляем уникальное название в список
            }
        }
    }
}