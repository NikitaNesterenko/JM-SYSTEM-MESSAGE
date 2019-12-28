package jm;

import jm.api.dao.ThreadChannelDAO;
import jm.model.ThreadChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ThreadChannelServiceImpl implements ThreadChannelService{

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
    public ThreadChannel findByChannelMessageId(Long id) {
        return threadChannelDao.getByChannelMessageId(id);
    }

}
