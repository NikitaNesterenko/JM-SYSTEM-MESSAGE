import {MessageRestPaginationService} from './rest/entities-rest-pagination.js'
import {pushMessage} from './functions/messageBox/pushMessage.js'
import {updateMessages} from './functions/messageBox/updateMessages.js'

const channel_id = 1;//Захардкоденные переменные
const message_service = new MessageRestPaginationService();

window.pushMessage = pushMessage(message);

window.updateMessages = updateMessages();

updateMessages();
