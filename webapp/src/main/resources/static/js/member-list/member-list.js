import {MemberEventHandler} from "./components/MemberEventHandler.js";
import {Members} from "./components/Members.js";

const memberList = new Members();
const attachMemberList = new MemberEventHandler(memberList);

$(document).ready(() => { attachMemberList.memberListHandler() });

export function attachMemberListBtnClickHandler() {
    attachMemberList.memberListHandler();
}

export function refreshMemberList() {
    memberList.showMemberList();
}