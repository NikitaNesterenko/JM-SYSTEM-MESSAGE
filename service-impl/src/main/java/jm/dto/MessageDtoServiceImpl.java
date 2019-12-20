package jm.dto;

import jm.api.dao.BotDAO;
import jm.api.dao.UserDAO;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.User;
import jm.model.message.Message;

import java.util.List;

public class MessageDtoServiceImpl implements MessageDtoService {

    private final UserDAO userDAO;
    private final BotDAO botDAO;

    public MessageDtoServiceImpl(UserDAO userDAO, BotDAO botDAO) {
        this.userDAO = userDAO;
        this.botDAO = botDAO;
    }

    @Override
    public MessageDTO toDto(Message message) {

        if (message == null) {
            return null;
        }

        MessageDTO messageDto = new MessageDTO();

        messageDto.setId(message.getId());

        Long userId = message.getUser().getId();
        Long botId = message.getBot().getId();
        if (userId != null) {
            User user = userDAO.getById(userId);
            messageDto.setUserId(user.getId());
        } else if (botId != null) {
            Bot bot = botDAO.getById(botId);
            messageDto.setBotId(bot.getId());
        }

        messageDto.setContent(message.getContent());
        messageDto.setDateCreate(message.getDateCreate());
        messageDto.setFilename(message.getFilename());
        messageDto.setIsDeleted(message.getIsDeleted());

//        Channel channel = message.getC
        return messageDto;

    }

    @Override
    public List<MessageDTO> toDto(List<Message> messages) {
        return null;
    }

    @Override
    public Message toEntity(MessageDTO messageDto) {
        return null;
    }
}
