package jm.dto;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import jm.model.message.DirectMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class DirectMessageDtoServiceImpl implements DirectMessageDtoService {

    private UserDAO userDAO;
    private BotDAO botDAO;
    private ChannelDAO channelDAO;
    private MessageDAO messageDAO;

    @Autowired
    public void setDirectMessageDtoServiceImpl(UserDAO userDAO, BotDAO botDAO, ChannelDAO channelDAO, MessageDAO messageDAO) {
        this.userDAO = userDAO;
        this.botDAO = botDAO;
        this.channelDAO = channelDAO;
        this.messageDAO = messageDAO;
    }

    @Override
    public List<DirectMessageDTO> toDto(List<DirectMessage> directMessages) {
        if (directMessages == null || directMessages.isEmpty()) {
            return Collections.emptyList();
        }

        List<DirectMessageDTO> directMessageDTOList = new ArrayList<>();
        for (DirectMessage directMessage: directMessages) {
            directMessageDTOList.add(toDto(directMessage));
        }

        return directMessageDTOList;
    }

    @Override
    public DirectMessageDTO toDto(DirectMessage directMessage) {

        if (directMessage == null) {
            return null;
        }

        DirectMessageDTO directMessageDTO = new DirectMessageDTO(directMessage);


        User user = directMessage.getUser();
        Bot bot = directMessage.getBot();
        if (user != null) {
            directMessageDTO.setUserId(user.getId());
            directMessageDTO.setUserName(user.getName());
        } else if (bot != null) {
            directMessageDTO.setBotId(bot.getId());
            directMessageDTO.setBotNickName(bot.getNickName());
        }

        // setting up 'channelName'
        Long channelId = directMessage.getChannelId();
        if (channelId != null) {
            Channel channel = channelDAO.getById(channelId);
            directMessageDTO.setChannelName(channel.getName());
        }

        // setting up 'sharedMessageId'
        Message sharedMessage = directMessage.getSharedMessage();
        if (sharedMessage != null) {
            directMessageDTO.setSharedMessageId(sharedMessage.getId());
        }

        // setting up 'recipientUserIds'
        Set<Long> recipientUserIds = directMessage.getRecipientUsers().stream().map(User::getId).collect(Collectors.toSet());
        directMessageDTO.setRecipientUserIds(recipientUserIds);

        // setting up 'parentMessageId'
        Message parentMessage = directMessage.getParentMessage();
        if (parentMessage != null) {
            directMessageDTO.setParentMessageId(parentMessage.getId());
        }

        directMessageDTO.setContent(directMessage.getContent());
        directMessageDTO.setDateCreate(directMessage.getDateCreate());

        return directMessageDTO;
    }

    @Override
    public DirectMessage toEntity(DirectMessageDTO directMessageDTO) {

        if (directMessageDTO == null) {
            return null;
        }

        DirectMessage directMessage = new DirectMessage(directMessageDTO);

        if (directMessageDTO.getUserId() != null) {
            directMessage.setUser(userDAO.getById(directMessageDTO.getUserId()));
        }

        directMessage.setContent(directMessageDTO.getContent());
        directMessage.setDateCreate(directMessageDTO.getDateCreate());

        Set<Long> recipientUserIds = directMessageDTO.getRecipientUserIds();
        List<User> recipientUsers = userDAO.getUsersByIds(recipientUserIds);
        directMessage.setRecipientUsers(new HashSet<>(recipientUsers));

        return directMessage;
    }
}
