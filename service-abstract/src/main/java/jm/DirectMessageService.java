package jm;

import jm.dto.DirectMessageDTO;
import jm.model.message.DirectMessage;
import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface DirectMessageService {

    DirectMessage getDirectMessageById (Long id);

    Optional<DirectMessageDTO> getDirectMessageDtoByMessageId (@NonNull Long messageId);

    void saveDirectMessage (DirectMessage directMessage);

    DirectMessage updateDirectMessage (DirectMessage directMessage);

    void deleteDirectMessage (Long id);

    List<DirectMessage> getMessagesByConversationId (Long id, Boolean isDeleted);

    DirectMessageDTO getDirectMessageDtoByDirectMessage(@NonNull DirectMessage directMessage);

    DirectMessage getDirectMessageByDirectMessageDto(@NonNull DirectMessageDTO directMessageDTO);

    List<DirectMessageDTO> getDirectMessageDtoListByDirectMessageList(@NonNull List<DirectMessage> directMessagesList);

    /**
     * Метод сравнивает логин авторизированного пользователя с логином пользователя,
     * сообщение которого должно быть изменено
     * Если авторизированный пользователь совершает попытку изменить сообщение, автором которого не является то false.
     * Иначе true.
     *
     * @param userLogin
     * @param userName
     * @return Boolean
     */
    Boolean checkingPermissionOnUpdate(@NonNull String userLogin, @NonNull String userName);

    /**
     * Метод сравнивает логин авторизированного пользователя с логином пользователя,
     * сообщение которого должно быть удалено.
     * Если авторизированный пользователь совершает попытку удалить сообщение, автором которого не является то false.
     * Иначе true.
     * @param userLogin
     * @param messageId
     * @return Boolean
     */
    Boolean checkingPermissionOnDelete(@NonNull String userLogin, @NonNull Long messageId);

}
