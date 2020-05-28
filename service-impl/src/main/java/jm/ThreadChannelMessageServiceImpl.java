package jm;

import jm.api.dao.ThreadChannelDAO;
import jm.api.dao.ThreadChannelMessageDAO;
import jm.api.dao.UserDAO;
import jm.dto.ThreadMessageDTO;
import jm.model.ThreadChannel;
import jm.model.User;
import jm.model.message.ThreadChannelMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ThreadChannelMessageServiceImpl implements ThreadChannelMessageService {

    private final ThreadChannelMessageDAO threadChannelMessageDAO;
    private final UserDAO userDAO;
    private final ThreadChannelDAO threadChannelDAO;
    private final WorkspaceService workspaceService;
    private final MessageService messageService;

    @Autowired
    public ThreadChannelMessageServiceImpl(ThreadChannelMessageDAO threadChannelMessageDAO, UserDAO userDAO, ThreadChannelDAO threadChannelDAO, MessageService messageService, WorkspaceService workspaceService) {
        this.threadChannelMessageDAO = threadChannelMessageDAO;
        this.userDAO = userDAO;
        this.threadChannelDAO = threadChannelDAO;
        this.messageService = messageService;
        this.workspaceService = workspaceService;
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

    @Override
    public ThreadChannelMessage getEntityFromDTO(ThreadMessageDTO threadMessageDTO) {
        if (threadMessageDTO == null) {
            return null;
        }

        User user = userDAO.getById(threadMessageDTO.getUserId());
        ThreadChannel threadChannel = threadChannelDAO.getByChannelMessageId(threadMessageDTO.getParentMessageId()).get();
        ThreadChannelMessage threadChannelMessage = new ThreadChannelMessage();
        threadChannelMessage.setUser(user);
        threadChannelMessage.setContent(threadMessageDTO.getContent());
        threadChannelMessage.setDateCreate(threadMessageDTO.getDateCreate());
        threadChannelMessage.setFilename(threadMessageDTO.getFilename());
        threadChannelMessage.setIsDeleted(threadMessageDTO.getIsDeleted());
        threadChannelMessage.setThreadChannel(threadChannel);
        threadChannelMessage.setParentMessage(messageService.getMessageById(threadMessageDTO.getParentMessageId()));
        threadChannelMessage.setWorkspace(workspaceService.getWorkspaceById(threadMessageDTO.getWorkspaceId()));

        return threadChannelMessage;
    }
}
