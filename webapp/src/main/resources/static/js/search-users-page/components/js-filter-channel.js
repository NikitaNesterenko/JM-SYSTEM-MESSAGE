export class JsFilterChannel {
    constructor(filterInput, filterUl) {
        this.filterInput = filterInput;
        this.filterUl = filterUl;
    }

    jsFilterChannel(){
        $('#filter-address').val('');
        this.filterInput.val($(this).text());
        this.filterInput.trigger('change');
        this.filterUl.fadeOut(100);
    }
}