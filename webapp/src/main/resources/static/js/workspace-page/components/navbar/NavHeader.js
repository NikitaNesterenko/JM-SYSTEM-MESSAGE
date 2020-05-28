export class NavHeader {

    constructor() {
        this.header = $('.p-classic_nav__model__title');
    }

    setChannelTitle(channel_icon, channel_name) {
        this.header
            .find('.p-classic_nav__model__title__name__button')
            .replaceWith(`
                <div class="p-classic_nav__model__title__name__button">
                    <i class="p-classic_nav__model__title__name__hash_icon">${channel_icon}</i>
                    <span class="p-classic_nav__model__title__info__name" id="chanelName">${channel_name}</span> 
                </div>
            `);

        return this;
    }

    setInfo(channelId, numOfPeople, numOfStarredItems, topic) {
        this.header
            .find('.p-classic_nav__model__title__info')
            .replaceWith(`
                <div class="p-classic_nav__model__title__info">
                    <button class="p-classic_nav__model__title__info__star">
                        <i class="material-icons" style="font-size: 18px; color: orange;">star</i>
                    </button>
                    <span class="p-classic_nav__model__title__info__sep">|</span>
                        <button class="p-classic_nav__model__title__info__members" id="memberList">
                        <i class="material-icons">people</i>
                    &nbsp;<i id="peopleInChat"> ${numOfPeople} </i>
                    </button>
                    <div class="channel-member-info">
                        <ul class="ul-channel-member-info">
                        
                        </ul>
                    </div>
                    <span class="p-classic_nav__model__title__info__sep">|</span>
                        <button class="p-classic_nav__model__title__info__pins">
                        <i class="material-icons">flag</i>
                    &nbsp;<i id="flaggedItems"> ${numOfStarredItems} </i>
                    </button>
                    <span class="p-classic_nav__model__title__info__sep">|</span>
                    <div class="p-classic_nav__model__title__info__item">
                        <div id="topic_string_block" class="p-classic_nav__model__title__info__topic__text">
                            <span id="topic_string" class="p-classic_nav__model__title__info__topic__content"> ${topic} </span>
                            <button id="topic_button" style="display: none;" class="p-classic_nav__model__title__info__topic__edit">
                                <i class="material-icons">edit</i>
                            </button>
                        </div>
                    </div>
                </div>
            `);
    }
}