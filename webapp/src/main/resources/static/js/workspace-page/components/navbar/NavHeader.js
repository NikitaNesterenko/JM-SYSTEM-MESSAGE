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
                    <span class="p-classic_nav__model__title__info__name">${channel_name}</span> 
                </div>
            `);

        return this;
    }

    setInfoChannel() {
        this.header
            .find('.p-classic_nav__model__title__info')
            .replaceWith(`
                <div class="p-classic_nav__model__title__info">
                    <button type="button" id="star" class="p-classic_nav__model__title__info__star">
                        <i  class="p-classic_nav__model__title__info__star__icon">☆</i>
                    </button>
                    <span class="p-classic_nav__model__title__info__sep">|</span>
                        <button class="p-classic_nav__model__title__info__members">
                        <i class="p-classic_nav__model__title__info__channel__members__icon">👨</i>
                    &nbsp;
                    -
                    <!--                                                               Members Count-->
                    </button>
                    <span class="p-classic_nav__model__title__info__sep">|</span>
                        <button class="p-classic_nav__model__title__info__pins">
                        <i class="p-classic_nav__model__title__info__pins__icon">📍</i>
                    &nbsp;
                    -
                    <!--                                                                  Pins Count-->
                    </button>
                    <span class="p-classic_nav__model__title__info__sep">|</span>
                    <div class="p-classic_nav__model__title__info__item">
                        <div id="topic_string_block" class="p-classic_nav__model__title__info__topic__text">
                            <span id="topic_string" class="p-classic_nav__model__title__info__topic__content">Enter channel topic here.</span>
                            <button id="topic_button" style="display: none;" class="p-classic_nav__model__title__info__topic__edit">
                                Edit
                            </button>
                        </div>
                    </div>
                </div>
            `);
    }

    setInfoConversation() {
        this.header
            .find('.p-classic_nav__model__title__info')
            .replaceWith(`
                    <div class="p-classic_nav__model__title__info">
                        <button type="button" id="star" class="p-classic_nav__model__title__conversation__info__star">
                            <i  class="p-classic_nav__model__title__conversation__info__star__icon">☆</i>
                        </button>
                        <span class="p-classic_nav__model__title__info__sep">|</span>
                            <button class="p-classic_nav__model__title__info__members">
                            <i class="p-classic_nav__model__title__info__channel__members__icon">👨</i>
                        &nbsp;
                        -
                        <!--                                                               Members Count-->
                        </button>
                        <span class="p-classic_nav__model__title__info__sep">|</span>
                            <button class="p-classic_nav__model__title__info__pins">
                            <i class="p-classic_nav__model__title__info__pins__icon">📍</i>
                        &nbsp;
                        -
                        <!--                                                                  Pins Count-->
                        </button>
                        <span class="p-classic_nav__model__title__info__sep">|</span>
                        <div class="p-classic_nav__model__title__info__item">
                            <div id="topic_string_block" class="p-classic_nav__model__title__info__topic__text">
                                <span id="topic_string" class="p-classic_nav__model__title__info__topic__content">Enter channel topic here.</span>
                                <button id="topic_button" style="display: none;" class="p-classic_nav__model__title__info__topic__edit">
                                    Edit
                                </button>
                            </div>
                        </div>
                    </div>
                `);
    }
}