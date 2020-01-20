import {findEl} from "../../filter-channel";


export class Propertychange {

    constructor(channelNames, filterUl) {
        this.channelNames = channelNames;
        this.filterUl = filterUl;
    }
    propertyChange(){
        if ($(this).val() !== '') {
            this.filterUl.fadeIn(100);
            findEl(this.filterUl, this.channelNames, $(this).val());
        } else {
            this.filterUl.fadeOut(100);
        }
    }
}