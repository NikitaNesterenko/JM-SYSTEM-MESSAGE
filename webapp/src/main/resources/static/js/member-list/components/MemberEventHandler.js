export class MemberEventHandler {

    constructor(members) {
        this.isListExpended = false;
        this.members = members;
    }

    memberListHandler() {
        const memberListBtn = $('#memberListBtn');
        if (memberListBtn !== null) {
            memberListBtn.click(this.members, this.btnOnClick);
            this.isListExpended = false;
        }
    }

    btnOnClick(event) {
        const memberListCaretSymbol = $('#memberListCaretSymbol');
        if (!this.isListExpended) {
            memberListCaretSymbol.text("▼");
            event.data.showMemberList();
        } else {
            memberListCaretSymbol.text("►");
            $("#memberListPlaceholder").text('');
        }
        this.isListExpended = !this.isListExpended;
    }
}