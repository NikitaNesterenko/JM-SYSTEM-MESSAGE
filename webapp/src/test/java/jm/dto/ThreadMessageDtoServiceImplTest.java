package jm.dto;

import jm.api.dao.ThreadChannelDAO;
import jm.api.dao.UserDAO;
import jm.model.Message;
import jm.model.ThreadChannel;
import jm.model.User;
import jm.model.message.ThreadChannelMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ThreadMessageDtoServiceImplTest {
  private ThreadMessageDtoServiceImpl threadMessageDtoService;
  private User user1 = new User("Robert", "Martin", "uncle_bob", "bob.uncle@tdd.com", "1234");
  private User user2 = new User("Joshua", "Bloch", "j_bloch", "j.bloch@java.com", "4321");
  private LocalDateTime localDateTime = LocalDateTime.now();

  @Mock UserDAO userDAO;
  @Mock ThreadChannelDAO threadChannelDAO;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    user1.setId(1L);
    user2.setId(2L);

    threadMessageDtoService = new ThreadMessageDtoServiceImpl();
    threadMessageDtoService.setUserDAO(userDAO);
    threadMessageDtoService.setThreadChannelDAO(threadChannelDAO);
  }

  @Test
  public void toDto_Should_Return_Null_If_ThreadChannelMessage_Is_Null() {
    assertNull(threadMessageDtoService.toDto((ThreadChannelMessage) null));
  }

  @Test
  public void toDto_Should_Return_ThreadMessageDTO() {
    Message message = new Message(3L, 1L, user1, "Message 1", localDateTime);
    ThreadChannel threadChannel = new ThreadChannel(message);

    ThreadChannelMessage threadChannelMessage = new ThreadChannelMessage();
    threadChannelMessage.setThreadChannel(threadChannel);
    threadChannelMessage.setId(20L);
    threadChannelMessage.setUser(user2);
    threadChannelMessage.setContent("Thread Message Content");
    threadChannelMessage.setDateCreate(localDateTime);
    threadChannelMessage.setIsDeleted(false);

    ThreadMessageDTO threadMessageDTO = threadMessageDtoService.toDto(threadChannelMessage);

    assertEquals(20L, (long) threadMessageDTO.getId());
    assertEquals(2L, (long) threadMessageDTO.getUserId());
    assertEquals("Joshua", threadMessageDTO.getUserName());
    assertEquals("Thread Message Content", threadMessageDTO.getContent());
    assertEquals(3L, (long) threadMessageDTO.getParentMessageId());
    assertEquals(localDateTime, threadMessageDTO.getDateCreate());
  }

  @Test
  public void testToDto() {}

  @Test
  public void toEntity() {}
}
