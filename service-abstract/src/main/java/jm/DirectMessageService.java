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

}
