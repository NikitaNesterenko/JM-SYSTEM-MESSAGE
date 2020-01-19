export class Add_empty_content_to_right_panel {
    constructor() {
    }

    AddEmptyContent(){
            return `<div class="starred-messages-empty">
                                                <div>
                                                    <img class="starred-messages-empty_img" src="../../image/empty_starred_posts.png">
                                                </div>
                                                <div class="starred-messages-empty_content">
                                                    <h5>
                                                        No starred items
                                                    </h5>
                                                    <p>
                                                        Star a message or file to save it here. Mark your to-dos, or 
                                                        save something for later — only you can see your starred items, so use them however you like!
                                                    </p>
                                                </div>
                                          </div>`;
        };
}