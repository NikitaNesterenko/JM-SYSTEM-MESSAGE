package jm;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.UserDAO;
import jm.api.dao.WorkspaceDAO;
import jm.dao.ChannelDAOImpl;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import jm.model.CreateWorkspaceToken;
import jm.model.User;
import jm.model.Workspace;
import lombok.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private final ChannelDAO channelDAO;
    private final UserDAO userDAO;
    private final BotDAO botDAO;
    private final WorkspaceDAO workspaceDAO;
    private final WorkspaceService workspaceService;
    private final UserService userService;
    private CreateWorkspaceTokenService createWorkspaceTokenService;
    private ChannelDTO channelDTO;
    private ChannelDAOImpl channelDaoImpl;

    public ChannelServiceImpl(ChannelDAO channelDAO, UserDAO userDAO, BotDAO botDAO, WorkspaceDAO workspaceDAO, WorkspaceService workspaceService, UserService userService, CreateWorkspaceTokenService createWorkspaceTokenService) {
        this.channelDAO = channelDAO;
        this.userDAO = userDAO;
        this.botDAO = botDAO;
        this.workspaceDAO = workspaceDAO;
        this.workspaceService = workspaceService;
        this.userService = userService;
        this.createWorkspaceTokenService = createWorkspaceTokenService;
    }

    @Override
    // TODO: ПРОВЕИТЬ
    // List<ChannelDTO> getAllChannels()
    public List<Channel> gelAllChannels() {
        return channelDAO.getAll();
    }

    @Override
    public List<ChannelDTO> getAllChanelDTO() {
        return channelDAO.getAllChanelDTO();
    }

    @Override
    public void createChannel(Channel channel) {
        channelDAO.persist(channel);
    }

    @Async("threadPoolTaskExecutor")
    @Override
    public void deleteChannel(Long id) {
        channelDAO.deleteById(id);
    }

    @Override
    public void updateChannel(Channel channel) {
        channelDAO.merge(channel);
    }

    @Override
    public void createChannelByTokenAndUsers(CreateWorkspaceToken createWorkspaceToken, Set<User> users) {
        createWorkspaceTokenService.updateCreateWorkspaceToken(createWorkspaceToken);
        Workspace workspace = workspaceService.getWorkspaceByName(createWorkspaceToken.getWorkspaceName());
        Channel channel = new Channel(
                createWorkspaceToken.getChannelname(),
                users,
                userService.getUserByEmail(createWorkspaceToken.getUserEmail()),
                false,
                LocalDateTime.now(), workspace);
        createChannel(channel);
    }

    @Override
    public Channel getChannelById(Long id) {
        return channelDAO.getById(id);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOById(Long id) {
        return channelDAO.getChannelDTOById(id);
    }

    @Override
    public Channel getChannelByName(String name) {
        return channelDAO.getChannelByName(name).orElse(null);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOByName(String name) {
        return channelDAO.getChannelDTOByName(name);
    }

    @Override
    public Optional<Long> getChannelIdByName(String chanelName) {
        return channelDAO.getChannelIdByName(chanelName);
    }

    @Override
    public List<Channel> getChannelsByOwnerId(Long ownerId) {
        try {
            return channelDAO.getChannelsByOwnerId(ownerId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId) {
        try {
            return channelDAO.getChannelByWorkspaceAndUser(workspaceId, userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) {
        try {
            return channelDAO.getChannelsByWorkspaceId(id);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChannelDTO> getChannelDtoListByWorkspaceId(Long workspaceId) {
        try {
            return channelDAO.getChannelDtoListByWorkspaceId(workspaceId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Channel> getChannelsByUserId(Long userId) {
        try {
            return channelDAO.getChannelsByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChannelDTO> getChannelDtoListByUserId(Long userId) {
        try {
            return channelDAO.getChannelDtoListByUserId(userId);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Channel getChannelByChannelDto(@NonNull ChannelDTO channelDTO) {

        Channel channel = new Channel(channelDTO);

        channel.setUsers(
                Optional.ofNullable(channelDTO.getUserIds())
                        .map(userIds -> userIds.stream().map(userDAO::getById).collect(Collectors.toSet()))
                        .orElse(new HashSet<>())
        );

        channel.setBots(
                Optional.ofNullable(channelDTO.getBotIds())
                        .map(botIds -> botIds.stream().map(botDAO::getById).collect(Collectors.toSet()))
                        .orElse(new HashSet<>())

        );

        Optional.ofNullable(channelDTO.getWorkspaceId()).ifPresent(workspaceID -> {
            channel.setWorkspace(workspaceDAO.getById(workspaceID));
        });

        Optional.ofNullable(channelDTO.getOwnerId()).ifPresent(ownerId -> channel.setUser(userDAO.getById(ownerId)));

        return channel;
    }

    @Override
    public List<ChannelDTO> getChannelDtoListByChannelList(@NonNull List<Channel> channelList) {
        return channelList.stream().map(this::getChannelDtoByChannel).collect(Collectors.toList());
    }

    @Override
    public ChannelDTO getChannelDtoByChannel(@NonNull Channel channel) {
        return new ChannelDTO(channel);
    }

    @Override
    public String getTopicChannelByChannelId(Long id) {
        return channelDAO.getTopicChannelByChannelId(id);
    }

    @Override
    public Workspace getWorkspaceByChannelId(Long channelId) {
        return channelDAO.getWorkspaceByChannelId(channelId);
    }

    @Override
    public List<ChannelDTO> getAllArchiveChannels() {
        try {
            return channelDAO.getArchivedChannels();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<ChannelDTO> getPrivateChannels() {
        try {
            return channelDAO.getPrivateChannels();
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public void unzipChannel(Channel channel) {
        channel.setArchived(false);
        channelDAO.unzipChannel(channel.getId());
    }
}

