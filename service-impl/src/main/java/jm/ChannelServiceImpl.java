package jm;

import jm.api.dao.ChannelDAO;
import jm.dto.ChannelDTO;
import jm.model.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private ChannelDAO channelDAO;

    @Autowired
    public void setChannelDAO (ChannelDAO channelDAO) {
        this.channelDAO = channelDAO;
    }

    @Override
    public List<Channel> gelAllChannels () {
        return channelDAO.getAll();
    }

    @Override
    public void createChannel (Channel channel) {
        channelDAO.persist(channel);
    }

    @Override
    public void deleteChannel (Long id) {
        channelDAO.deleteById(id);
    }

    @Override
    public void updateChannel (Channel channel) {
        channelDAO.merge(channel);
    }

    @Override
    public Channel getChannelById (Long id) {
        return channelDAO.getById(id);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOById (Long id) {
        //TODO: Удалить sout
//        System.out.println("Получить DTO по ID");
        Optional<ChannelDTO> channelDTO = channelDAO.getChannelDTOByIdWithoutFieldsUserIdsAndBotIds(id);
//        System.out.println("channelDTO без сетов: " + channelDTO.get().toString());
        if (channelDTO.isPresent()) {
            String name = channelDTO.get()
                                  .getName();
            channelDTO.get()
                    .setBotIds(channelDAO.getSetBotIdsByName(name));
            channelDTO.get()
                    .setUserIds(channelDAO.getSetUserIdsByName(name));
        }
//        System.out.println("С сетами " + channelDTO.get().toString());
        return channelDTO;
    }

    @Override
    public Channel getChannelByName (String name) {
        return channelDAO.getChannelByName(name);
    }

    @Override
    public Optional<ChannelDTO> getChannelDTOByName (String name) {
        //TODO: удалить лишнее
        Optional<ChannelDTO> channelDTOByName = channelDAO.getChannelDTOByNameWithoutFieldsUserIdsAndBotIds(name);
        System.out.println("channelDTOByName: " + channelDTOByName.toString());
//        Optional<ChannelDTO> channelDTOById = channelDAO.getChannelDTOByIdWithoutFieldsUserIdsAndBotIds(1L);
//        System.out.println("channelDTOById: " + channelDTOById.toString());
//        Set<Long> userIds = channelDAO.getSetUserIdsByName(name);
//        System.out.println("userIds: " + userIds);
//        Set<Long> botIds = channelDAO.getSetBotIdsByName(name);
//        System.out.println("botIds: " + botIds);
//        Optional<ChannelDTO> id = channelDAO.getIdByName(name);
//        System.out.println("id: " + id.get().getId());


        getChannelDTOById(channelDTOByName.get()
                                  .getId());
        Optional<ChannelDTO> channelDTO = channelDAO.getChannelDTOByNameWithoutFieldsUserIdsAndBotIds(name);
//        System.out.println("channelDTO без сетов: " + channelDTO.get().toString());
        if (channelDTO.isPresent()) {
            channelDTO.get()
                    .setBotIds(channelDAO.getSetBotIdsByName(name));
            channelDTO.get()
                    .setUserIds(channelDAO.getSetUserIdsByName(name));
        }

        return channelDAO.getChannelDTOByNameWithoutFieldsUserIdsAndBotIds(name);
    }

    @Override
    public List<Channel> getChannelsByOwnerId (Long ownerId) {
        return channelDAO.getChannelsByOwnerId(ownerId);
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser (Long workspaceId, Long userId) {
        return channelDAO.getChannelByWorkspaceAndUser(workspaceId, userId);
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId (Long id) {
        return channelDAO.getChannelsByWorkspaceId(id);
    }

    @Override
    public List<Channel> getChannelsByUserId (Long userId) {
        return channelDAO.getChannelsByUserId(userId);
    }

}

