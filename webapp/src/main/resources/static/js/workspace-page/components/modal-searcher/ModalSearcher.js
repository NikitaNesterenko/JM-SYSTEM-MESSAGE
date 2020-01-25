export class ModalSearcher {
    channels;
    users;
    input = "";

    setSearchData(search_data) {
        this.channels = search_data.available_channels;
        this.users = search_data.available_users;
    };

    onSearchClick() {
        $("#buttonSearcher").click(() => {
            $("#modalSearcher").css("display", "block");
            this.showSearchResult();
        });
        return this;
    }

    onInputSelection() {
        $("#idSearchContent").on("click", "li.search-field-li", function () {
            const id = $(this).data("id");
            const type = $(this).data("type");
            if (type === "channel") {
                pressChannelButton(id);
                sessionStorage.setItem("channelName", id);
            } else {
                console.log("Открытие личной переписки с User с id=" + id);
            }
            $("#modalSearcher").modal("hide");
        });
        return this;
    }

    inputSearchBind() {
        $('#searchInput').bind("change paste keyup", (event) => {
            this.input = event.target.value;
            this.showSearchResult();
        });
    }

    showSearchResult() {
        const channels_filtered = this.filterChannels();
        const users_filtered = this.filterUsers();
        $('#idSearchContent').html('<ol role="listbox">'
            + '<div class="search_channels_in_modal" id="search_channel_in_modal_id">'
            + this.showChannels(channels_filtered)
            + '</div>'
            + '<div class="search_users_in_modal">'
            + this.showUsers(users_filtered)
            + '</div>'
            + '</ol>')
    }

    showChannels(channels) {
        return channels.map(
            (channel) => this.displayItem(channel.id, "channel", channel.name, this.channelPic(channel))
        ).join("");
    }

    showUsers(users) {
        return users.map(
            (user) => this.displayItem(user.id, "user",user.name + " " + user.lastName, this.userPic())
        ).join("");
    }

    displayItem(id, itemClass, itemName, pic) {
        return '<li class="search-field-li" data-type="' + itemClass + '" data-id="' + id + '">'
            + '<div class="search-field-name">'
            + pic
            + '<span>'
            + itemName
            + '</span>'
            + '</div>'
            + '</li>';
    }

    channelPic(channel) {
        let pic = "#";
        if (channel.isPrivate) {
            pic = "*";
        }
        return '<i class="searcher__channel_icon_prefix">'
            + pic
            + '</i>';
    }

    userPic() {
        return '<i class="searcher__channel_icon_prefix">'
            + '@'
            + '</i>';
    }

    filterUsers() {
        return this.users.filter(el => (el.name + " " + el.lastName).toLowerCase().indexOf(this.input.toLowerCase()) >= 0);

    }

    filterChannels() {
        return this.channels.filter(el => el.name.toLowerCase().indexOf(this.input.toLowerCase()) >= 0);
    }
}