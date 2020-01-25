import {ChannelFilter} from "./components/ChannelFilter.js";


const filter = new ChannelFilter();
filter.onChange().onClick();

$(document).ready(() => {
    filter.getAllChannels();
});