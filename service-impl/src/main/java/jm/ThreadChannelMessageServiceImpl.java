package jm;

import jm.api.dao.ThreadChannelMessageDAO;
import jm.dto.ThreadMessageDTO;
import jm.model.ThreadChannel;
import jm.model.message.ThreadChannelMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ThreadChannelMessageServiceImpl implements ThreadChannelMessageService {

    private ThreadChannelMessageDAO threadChannelMessageDAO;

    @Autowired
    public void setThreadChannelMessageDAO(ThreadChannelMessageDAO threadChannelMessageDAO) {
        this.threadChannelMessageDAO = threadChannelMessageDAO;
    }

    @Override
    public void createThreadChannelMessage(ThreadChannelMessage threadChannelMessage) {
        threadChannelMessageDAO.persist(threadChannelMessage);
    }

    @Override
    public List<ThreadChannelMessage> findAll() {
        return threadChannelMessageDAO.getAll();
    }

    @Override
    public List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannel(ThreadChannel threadChannel) {
        return threadChannelMessageDAO.findAllThreadChannelMessagesByThreadChannel(threadChannel);
    }

    @Override
    public List<ThreadChannelMessage> findAllThreadChannelMessagesByThreadChannelId(Long id) {
        return threadChannelMessageDAO.findAllThreadChannelMessagesByThreadChannelId(id);
    }

    @Override
    public List<ThreadMessageDTO> getAllThreadMessageDTOByThreadChannelId(Long id) {
        return findAllThreadChannelMessagesByThreadChannelId(id)
                .stream()
                .map(ThreadMessageDTO::new)
                .collect(Collectors.toList());

    }
}
