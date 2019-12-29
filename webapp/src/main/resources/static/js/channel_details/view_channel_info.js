import {ChannelInfoPanel} from "./components/ChannelInfoPanel.js";
import {HighlightsItem} from "./components/HighlightsItem.js";
import {ChannelDetailsItem} from "./components/ChannelDetailsItem.js";
import {PinnedItem} from "./components/PinnedItem.js";
import {MembersItem} from "./components/MembersItem.js";
import {AppsItem} from "./components/AppsItem.js";
import {SharedFilesItem} from "./components/SharedFilesItem.js";
import {NotificationPreferencesItem} from "./components/NotificationPreferencesItem.js";

const channelDetails = new ChannelInfoPanel({root: '.p-classic_nav__model__button__details'});

const items = [
    new ChannelDetailsItem(),
    new HighlightsItem(),
    new PinnedItem(),
    new MembersItem(),
    new AppsItem(),
    new SharedFilesItem(),
    new NotificationPreferencesItem()
];

channelDetails.addItems(items);
