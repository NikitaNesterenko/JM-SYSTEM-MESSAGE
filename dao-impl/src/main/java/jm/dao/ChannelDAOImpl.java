package jm.dao;

import jm.api.dao.ChannelDAO;
import jm.model.Channel;
import jm.dto.ChannelDTO;
import jm.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class ChannelDAOImpl extends AbstractDao<Channel> implements ChannelDAO {
    private static final Logger logger = LoggerFactory.getLogger(ChannelDAOImpl.class);

    @Override
    public Optional<Channel> getChannelByName(String name) {
        return Optional.ofNullable((Channel) entityManager.createNativeQuery("SELECT * FROM channels WHERE name=?", Channel.class)
                    .setParameter(1, name)
                    .getSingleResult());
    }

    @Override
    public Optional<List<Channel>> getChannelsByOwner(User user) {
            return Optional.ofNullable((List<Channel>) entityManager.createNativeQuery("SELECT * FROM channels WHERE owner_id=?", Channel.class)
                    .setParameter(1, user.getId()).getResultList());
    }

    @Override
    public List<ChannelDTO> getChannelByWorkspaceAndUser(Long workspaceId, Long userId) {
        String query = "SELECT ch.id, ch.name, ch.is_private " +
                "FROM channels ch " +
                "LEFT JOIN channels_users chu ON chu.channel_id = ch.id " +
                "LEFT JOIN workspaces ws ON ch.workspace_id = ws.id " +
                "LEFT JOIN users u ON chu.user_id = u.id " +
                "WHERE (ws.id = :workspace_id AND ((u.id = :user_id AND ch.is_private = true) OR ch.is_private = false)) " +
                "GROUP BY ch.id";

        return entityManager.createNativeQuery(query, "ChannelDTOMapping")
                .setParameter("workspace_id", workspaceId)
                .setParameter("user_id", userId)
                .getResultList();
    }

    @Override
    public List<Channel> getChannelsByWorkspaceId(Long id) {
            return (List<Channel>) entityManager.createNativeQuery("SELECT * FROM channels WHERE workspace_id=?", Channel.class)
                    .setParameter(1, id)
                    .getResultList();
    }

    public  List<Channel> getChannelsByUserId(Long userId) {
            List<BigInteger> channelsIdentityNumbers = (List<BigInteger>) entityManager.createNativeQuery("SELECT channel_id FROM channels_users WHERE user_id=?")
                    .setParameter(1, userId)
                    .getResultList();
            List<BigInteger> channelsIdentityNumbersArrayList = new ArrayList<>(channelsIdentityNumbers);
            List<Channel> channels = new ArrayList<>();
            for(BigInteger channelId : channelsIdentityNumbersArrayList) {
                Long channelLongId = channelId.longValue();
                Channel channel = getById(channelLongId);
                System.out.println(channel);
                channels.add(channel);
            }
            return channels;
    }
}
