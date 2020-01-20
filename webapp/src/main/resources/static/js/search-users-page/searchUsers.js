import {ShowAllChannels} from "./components/showAllChannels.js"
import {Propertychange} from "./components/propertychange.js"
import {JsFilterChannel} from "./components/js-filter-channel.js"

const filterInput = $('#filter-channel'), filterUl = $('.ul-channel');
let channelNames = [];

const showAllChannels = new ShowAllChannels(channelNames);
$(document).ready(() => {
    showAllChannels.showAllChannels();
});

//проверка при каждом вводе символа
const propertychange = new Propertychange(channelNames, filterUl);
filterInput.on('input propertychange', propertychange.propertyChange());

//при клике на елемент выпадалки присваиваем значение в инпут и ставим триггер на его изменение
const jsFilterChannel = new JsFilterChannel(filterInput, filterUl)
filterUl.on('click', '.js-filter-channel', jsFilterChannel.jsFilterChannel());