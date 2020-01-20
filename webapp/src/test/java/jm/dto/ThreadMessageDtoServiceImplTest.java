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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class ThreadMessageDtoServiceImplTest {
  private ThreadMessageDtoServiceImpl threadMessageDtoService;
  private ThreadChannelMessage threadChannelMessage;
  private ThreadChannel threadChannel;
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

    Message message = new Message(3L, 1L, user1, "Message 1", localDateTime);
    threadChannel = new ThreadChannel(message);

    threadChannelMessage = new ThreadChannelMessage();
    threadChannelMessage.setThreadChannel(threadChannel);
    threadChannelMessage.setId(20L);
    threadChannelMessage.setUser(user2);
    threadChannelMessage.setContent("Thread Message Content");
    threadChannelMessage.setDateCreate(localDateTime);
    threadChannelMessage.setIsDeleted(false);
  }

  @Test
  public void toDto_Should_Return_Null_If_ThreadChannelMessage_Is_Null() {
    assertNull(threadMessageDtoService.toDto((ThreadChannelMessage) null));
  }

  @Test
  public void toDto_Should_Return_ThreadMessageDTO() {
    ThreadMessageDTO threadMessageDTO = threadMessageDtoService.toDto(threadChannelMessage);

    assertEquals(20L, (long) threadMessageDTO.getId());
    assertEquals(2L, (long) threadMessageDTO.getUserId());
    assertEquals("Joshua", threadMessageDTO.getUserName());
    assertEquals("Thread Message Content", threadMessageDTO.getContent());
    assertEquals(3L, (long) threadMessageDTO.getParentMessageId());
    assertEquals(localDateTime, threadMessageDTO.getDateCreate());
  }

  @Test
  public void toDto_Should_Return_Empty_List_If_ThreadChannelMessage_List_Is_Empty_Or_Null() {
    List<ThreadMessageDTO> threadList = threadMessageDtoService.toDto(new ArrayList<>());
    assertEquals(0, threadList.size());

    threadList = threadMessageDtoService.toDto((List<ThreadChannelMessage>) null);
    assertEquals(0, threadList.size());
  }

  @Test
  public void toDto_Should_Return_ThreadMessageDTO_list() {
    List<ThreadMessageDTO> threadMessageDTOList =
        threadMessageDtoService.toDto(Collections.singletonList(threadChannelMessage));

    assertEquals(20L, (long) threadMessageDTOList.get(0).getId());
    assertEquals(2L, (long) threadMessageDTOList.get(0).getUserId());
    assertEquals("Joshua", threadMessageDTOList.get(0).getUserName());
    assertEquals("Thread Message Content", threadMessageDTOList.get(0).getContent());
    assertEquals(3L, (long) threadMessageDTOList.get(0).getParentMessageId());
    assertEquals(localDateTime, threadMessageDTOList.get(0).getDateCreate());
  }

  @Test
  public void toEntity_Should_Return_Null_If_ThreadMessageDTO_Is_Null() {
    assertNull(threadMessageDtoService.toEntity(null));
  }

  @Test
  public void toEntity_Should_Return_ThreadChannelMessage() {
    when(userDAO.getById(2L)).thenReturn(user2);
    when(threadChannelDAO.getByChannelMessageId(3L)).thenReturn(threadChannel);

    ThreadMessageDTO threadMessageDTO = new ThreadMessageDTO();
    threadMessageDTO.setContent("Thread DTO to Entity");
    threadMessageDTO.setDateCreate(localDateTime);
    threadMessageDTO.setUserId(2L);
    threadMessageDTO.setIsDeleted(false);
    threadMessageDTO.setFilename("c:/file.txt");
    threadMessageDTO.setParentMessageId(3L);

    ThreadChannelMessage threadChannelMessage = threadMessageDtoService.toEntity(threadMessageDTO);

    assertEquals(user2, threadChannelMessage.getUser());
    assertEquals(threadChannel, threadChannelMessage.getThreadChannel());
    assertEquals("Thread DTO to Entity", threadChannelMessage.getContent());
    assertEquals(localDateTime, threadChannelMessage.getDateCreate());
    assertFalse(threadChannelMessage.getIsDeleted());
    assertEquals("c:/file.txt", threadChannelMessage.getFilename());
  }
}
