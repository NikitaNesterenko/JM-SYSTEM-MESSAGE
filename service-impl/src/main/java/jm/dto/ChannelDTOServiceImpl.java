package jm.dto;

import jm.BotService;
import jm.UserService;
import jm.WorkspaceService;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ChannelDTOServiceImpl implements ChannelDtoService {

    @Autowired
    UserService userService;

    @Autowired
    BotService botService;

    @Autowired
    WorkspaceService workspaceService;

    @Override
    // channelDtoService.toDto
    public ChannelDTO toDto(Channel channel) {
        if (channel == null) {
            return null;
        }

        ChannelDTO channelDTO = new ChannelDTO();

        Set<Long> userIds = channel.getUsers().stream().map(User::getId).collect(Collectors.toSet());
//        channelDTO.setUserIds(userIds);

        Set<Long> botIds = channel.getBots().stream().map(Bot::getId).collect(Collectors.toSet());
//        channelDTO.setBotIds(botIds);

        channelDTO.setId(channel.getId());
        channelDTO.setName(channel.getName());
        channelDTO.setWorkspaceId(channel.getWorkspace().getId());
        channelDTO.setOwnerId(channel.getUser().getId());
        channelDTO.setUsername(channel.getName());
        channelDTO.setIsPrivate(channel.getIsPrivate());
        channelDTO.setTopic(channel.getTopic());
        channelDTO.setIsApp(channel.getIsApp());
//        channelDTO.setCreatedDate(channel.getCreatedDate());

        channelDTO.setIsArchived(channel.getArchived());
        channelDTO.setIsApp(channel.getIsApp());

        return channelDTO;
    }


    @Override
    public List<ChannelDTO> toDto(List<Channel> channels) {
        if (channels==null) {
            return null;
        }

        List<ChannelDTO> channelDTOList = new ArrayList<>();
        for (Channel channel : channels) {
            channelDTOList.add(toDto(channel));
        }
        return channelDTOList;
    }

    @Override
    public Channel toEntity(ChannelDTO channelDTO) {
        if (channelDTO==null) {
            return null;
        }

        Channel channel = new Channel();

        if (channelDTO.getUserIds()!=null) {
            Set<User> userSet = new HashSet<>();
            for (Long id : channelDTO.getUserIds()) {
                userSet.add(userService.getUserById(id));
            }
            channel.setUsers(userSet);
        }

        if (channelDTO.getBotIds()!=null) {
            Set<Bot> botSet = new HashSet<>();
            for (Long id : channelDTO.getBotIds()) {
                botSet.add(botService.getBotById(id));
            }
            channel.setBots(botSet);
        }

        if (channelDTO.getWorkspaceId()!=null) {
            channel.setWorkspace(
                    workspaceService.getWorkspaceById(channelDTO.getWorkspaceId()));
        }
        User userOwner = userService.getUserById(channelDTO.getOwnerId());


        channel.setId(channelDTO.getId());
        channel.setName(channelDTO.getName());
        channel.setUser(userOwner);
        channel.setIsPrivate(channelDTO.getIsPrivate());
        channel.setArchived(false);
        channel.setCreatedDate(channelDTO.getCreatedDate());

        if (channelDTO.getTopic()!=null) {
            channel.setTopic(channelDTO.getTopic());
        }

        channel.setIsApp(channelDTO.getIsApp());

        return channel;
    }
}
