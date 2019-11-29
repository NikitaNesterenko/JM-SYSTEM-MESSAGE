import {ChannelRestPaginationService, UserRestPaginationService} from './rest/entities-rest-pagination.js'

const channel_service = new ChannelRestPaginationService();
const user_service = new UserRestPaginationService();

let available_users;
let available_channels;

window.addEventListener('load', function () {
    const searcher_modal = document.getElementById("modalSearcher");
    const searcher_btn = document.getElementById("buttonSearcher");
    searcher_btn.onclick = function () {
        searcher_modal.style.display = "block";
        const load_channels = channel_service.getChannelsByWorkspaceAndUser(1,1);
        const load_users = user_service.getUsersByWorkspace(1);
        Promise.all([load_channels, load_users]).then(values => {
            available_channels = values[0];
            available_users = values[1];
            showSearchResult(available_channels, available_users);
        })
    };
});

function showSearchResult(channels, users) {
    $('#idSearchContent').html('<ol role="listbox">'
        + showChannels(channels)
        + showUsers(users)
        + '</ol>')
}

function showChannels(channels) {
    return channels.map((channel) => displayItem(channel.name)).join("");
}

function showUsers(users) {
    return users.map((user) => displayItem(user.name + " " + user.lastName)).join("");
}

function displayItem(itemName) {
    return '<li class="search-field-li">'
        + '<div class="search-field-name">'
        + itemName
        + '</div>'
        + '</li>';
}

$('#searchInput').bind("change paste keyup", function() {
    const search_str = ($(this).val());
    const searched_channel = available_channels.filter((el) => {
        return el.name.toLowerCase().indexOf(search_str.toLowerCase()) >= 0;
    });
    const searched_users = available_users.filter((el) => {
        const full_name = el.name + " " + el.lastName;
        return full_name.toLowerCase().indexOf(search_str.toLowerCase()) >= 0;
    });
    showSearchResult(searched_channel, searched_users);
});

