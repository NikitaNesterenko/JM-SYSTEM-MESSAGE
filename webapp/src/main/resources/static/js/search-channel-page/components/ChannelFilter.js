import {UserRestPaginationService} from "/js/rest/entities-rest-pagination.js";
import {findEl} from "/js/filter-channel.js"

export class ChannelFilter {

    constructor() {
        this.user_service = new UserRestPaginationService();
        this.filter_input = $('#filter-channel');
        this.filterUl = $('.ul-channel');
        this.channel_names = [];
    }

    getAllChannels() {
        this.user_service.getAll().then(
            channels => {
                $.each(channels, (i, item) => {
                    this.channel_names.push(item.name);
                })
            }
        )
    }

    onChange() {
        this.filter_input.on('input propertychange', this, function (event) {
            if ($(this).val() !== '') {
                event.data.filterUl.fadeIn(100);
                findEl(event.data.filterUl, event.data.channel_names, $(this).val());
            } else {
                event.data.filterUl.fadeOut(100);
            }
        });
        return this;
    }

    onClick() {
        this.filterUl.on('click', '.js-filter-channel', this, function (event) {
            $('#filter-address').val('');
            event.data.filter_input.val($(this).text());
            event.data.filter_input.trigger('change');
            event.data.filterUl.fadeOut(100);
        })
    }
}