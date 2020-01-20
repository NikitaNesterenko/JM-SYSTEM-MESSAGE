package jm.dto;


import jm.model.Message;
import jm.model.ThreadChannel;
import jm.model.User;
import org.springframework.stereotype.Service;

@Service
public class ThreadDtoServiceImpl implements ThreadDtoService {
    @Override
    public ThreadDTO toDto(ThreadChannel threadChannel) {
        if (threadChannel == null) {
            return null;
        }
        Message message = threadChannel.getMessage();
        User user = message.getUser();
        MessageDTO messageDTO = new MessageDTO(message);

        messageDTO.setUserId(user.getId());
        messageDTO.setUserName(user.getName());

        return new ThreadDTO(threadChannel.getId(), messageDTO);
    }

    @Override
    public ThreadChannel toEntity(ThreadDTO threadDTO) {
        return null;
    }
}
