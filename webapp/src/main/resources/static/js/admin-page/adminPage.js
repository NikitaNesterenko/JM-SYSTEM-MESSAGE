let users = [];// all users from the database; updated every time after editing and updating any user

let addEventListener = new AddEventListener();
window.addEventListener('load', addEventListener.addEventListener() );

let refreshUserList = new RefreshUserList();
refreshUserList.refreshUserList();

let showEditUserModal = new ShowEditUserModal(element);
showEditUserModal.showEditUserModal();

let documentReady = new DocumentReady();
$(document).ready(documentReady.documentReady());

let filterUserList = new FilterUserList();
filterUserList.filterUserList();

let showFilteredUsers = new ShowFilteredUsers();
showFilteredUsers.showFilteredUsers();