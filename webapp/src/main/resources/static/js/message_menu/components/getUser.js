class GetUser {

    async getUser(){
        const user = await user_service.getLoggedUser();
        return [user];
    }
}