package jm;

import jm.api.dao.MessageDAO;
import jm.api.dao.ThreadChannelDAO;
import jm.dto.MessageDTO;
import jm.dto.ThreadDTO;
import jm.model.ThreadChannel;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class ThreadChannelServiceImpl implements ThreadChannelService {

    private final ThreadChannelDAO threadChannelDao;
    private final MessageDAO messageDAO;

    public ThreadChannelServiceImpl(ThreadChannelDAO threadChannelDao, MessageDAO messageDAO) {
        this.threadChannelDao = threadChannelDao;
        this.messageDAO = messageDAO;
    }


    @Override
    public void createThreadChannel(ThreadChannel threadChannel) {
        threadChannelDao.persist(threadChannel);
    }

    @Override
    public ThreadChannel findByChannelMessageId(Long id) {
        return threadChannelDao.getByChannelMessageId(id);
    }

    @Override
    public Optional<ThreadDTO> getThreadDtoByChannelMessageId(@NonNull Long channelMessageId) {
        ThreadDTO threadDTO = null;

        Optional<Number> threadIdOptional = threadChannelDao.getThreadIdByChannelMessageId(channelMessageId);
        Optional<MessageDTO> messageDTOOptional = messageDAO.getMessageDtoById(channelMessageId);

        if (threadIdOptional.isPresent() && messageDTOOptional.isPresent()) {
            threadDTO = new ThreadDTO(threadIdOptional.get().longValue(), messageDTOOptional.get());
        }

        return Optional.ofNullable(threadDTO);
    }
}
