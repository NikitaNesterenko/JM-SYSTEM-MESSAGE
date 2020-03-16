package jm;

import jm.api.dao.ThreadChannelDAO;
import jm.dto.MessageDTO;
import jm.model.Message;
import jm.model.ThreadChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class ThreadChannelServiceImpl implements ThreadChannelService {
    private static final Logger logger = LoggerFactory.getLogger(
            ThreadChannelServiceImpl.class);

    private ThreadChannelDAO threadChannelDao;



    @Autowired
    public void setThreadChannelDao(ThreadChannelDAO threadChannelDao) {
        this.threadChannelDao = threadChannelDao;
    }

    @Override
    public void createThreadChannel(ThreadChannel threadChannel) {
        threadChannelDao.persist(threadChannel);
    }

    @Override
    public ThreadChannel createThreadChannelByMessageDTO(MessageDTO messageDTO) {
        messageDTO.setDateCreate(LocalDateTime.now());
        Message message = new Message(messageDTO);
        ThreadChannel threadChannel = new ThreadChannel(message);
        createThreadChannel(threadChannel);
        logger.info("Созданный тред : {}", threadChannel);
        return threadChannel;
    }

    @Override
    public ThreadChannel findByChannelMessageId(Long id) {
        return threadChannelDao.getByChannelMessageId(id);
    }

}
