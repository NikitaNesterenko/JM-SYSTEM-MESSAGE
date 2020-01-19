export class GetUserAndMessage {
    constructor(id) {
        this.id = id;
    }
    getUserAndMessage = async () =>{
            const user = await user_service.getLoggedUser();
            const msg = await message_service.getById(id);
            return [user, msg];
        };

}