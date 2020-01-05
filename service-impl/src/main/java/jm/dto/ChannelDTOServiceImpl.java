package jm.dto;

import jm.BotService;
import jm.UserService;
import jm.WorkspaceService;
import jm.model.Bot;
import jm.model.Channel;
import jm.model.User;
import jm.model.Workspace;
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
    public ChannelDTO toDto(Channel channel) {
        if (channel == null) { return null; }

        ChannelDTO channelDTO = new ChannelDTO();

        Set<Long> userIds = channel.getUsers().stream().map(User::getId).collect(Collectors.toSet());
        channelDTO.setUserIds(userIds);

        Set<Long> botIds = channel.getBots().stream().map(Bot::getId).collect(Collectors.toSet());
        channelDTO.setBotIds(botIds);

        channelDTO.setId(channel.getId());
        channelDTO.setName(channel.getName());
        channelDTO.setWorkspaceId(channel.getWorkspace().getId());
        channelDTO.setOwnerId(channel.getUser().getId());
        channelDTO.setIsPrivate(channel.getIsPrivate());
        channelDTO.setTopic(channel.getTopic());

        return channelDTO;
    }

    @Override
    public List<ChannelDTO> toDto(List<Channel> channels) {
        if (channels==null) { return null; }
        List<ChannelDTO> channelDTOList = new ArrayList<>();
        for (Channel channel : channels) { channelDTOList.add(toDto(channel)); }
        return channelDTOList;
    }

    @Override
    public Channel toEntity(ChannelDTO channelDTO) {
        if (channelDTO==null) { return null; }

        Set<User> userSet = new HashSet<>();
        for (Long id : channelDTO.getUserIds()) { userSet.add(userService.getUserById(id)); }

        Set<Bot> botSet = new HashSet<>();
        for (Long id : channelDTO.getBotIds()) { botSet.add(botService.getBotById(id)); }

        Workspace workspace = workspaceService.getWorkspaceById(channelDTO.getWorkspaceId());

        User userOwner = userService.getUserById(channelDTO.getOwnerId());

        Channel channel = new Channel();
        channel.setId(channelDTO.getId());
        channel.setName(channelDTO.getName());
        channel.setUsers(userSet);
        channel.setBots(botSet);
        channel.setWorkspace(workspace);
        channel.setUser(userOwner);
        channel.setIsPrivate(channelDTO.getIsPrivate());
        channel.setArchived(false);
        channel.setCreatedDate(channelDTO.getCreatedDate());
        channel.setTopic(channelDTO.getTopic());

        return channel;
    }
}
