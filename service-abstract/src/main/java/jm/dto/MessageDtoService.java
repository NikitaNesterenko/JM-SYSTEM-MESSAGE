package jm.dto;

import jm.model.message.Message;

import java.util.List;

public interface MessageDtoService {

    MessageDTO toDto(Message message);

    List<MessageDTO> toDto(List<Message> messages);

    Message toEntity(MessageDTO messageDto);

}
