package jm.dto;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.model.Channel;
import jm.model.Message;
import jm.model.User;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class MessageDtoServiceImplTest {
  private MessageDtoService messageDtoService;
  private Message message;
  private Message sharedMessage;

  private LocalDateTime localDateTime = LocalDateTime.now();
  private User user1 = new User("Robert", "Martin", "uncle_bob", "bob.uncle@tdd.com", "1234");
  private User user2 = new User("Joshua", "Bloch", "j_bloch", "j.bloch@java.com", "4321");
  private Channel channel = new Channel();

  @Mock UserDAO userDAO;
  @Mock BotDAO botDAO;
  @Mock ChannelDAO channelDAO;
  @Mock MessageDAO messageDAO;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    messageDtoService = new MessageDtoServiceImpl(userDAO, botDAO, channelDAO, messageDAO);

    user1.setId(1L);
    user1.setAvatarURL("image_1.jpg");

    user2.setId(2L);
    user2.setAvatarURL("image_2.jpg");

    sharedMessage = new Message(2L, 1L, user2, "Message Shared", localDateTime);
    sharedMessage.setChannelId(2L);

    message = new Message(3L, 1L, user1, "Message 1", localDateTime);
    message.setChannelId(1L);
    message.setSharedMessage(sharedMessage);
    message.setRecipientUsers(new HashSet<>());

    channel.setId(1L);
    channel.setName("Channel Name");
  }

  @Test
  public void toDto_Should_Return_Null_If_Message_Is_Null() {
    // messageDtoService.toDto 123456 OK
    assertNull(messageDtoService.toDto((Message) null));
  }

  @Test
  public void toDto_Should_Return_MessageDTO() {
    when(channelDAO.getById(1L)).thenReturn(channel);
      // messageDtoService.toDto 123456 NOT OK
    MessageDTO messageDTO = messageDtoService.toDto(message);

    assertEquals(3L, (long) messageDTO.getId());
    assertEquals("Message 1", messageDTO.getContent());
    assertEquals(localDateTime, messageDTO.getDateCreate());
    assertNull(messageDTO.getFilename());
    assertFalse(messageDTO.getIsDeleted());
    assertEquals(1L, (long) messageDTO.getChannelId());
    assertEquals(1L, (long) messageDTO.getUserId());
    assertEquals("Robert", messageDTO.getUserName());
    assertEquals("image_1.jpg", messageDTO.getUserAvatarUrl());
    assertEquals("Channel Name", messageDTO.getChannelName());
    assertEquals(2L, (long) messageDTO.getSharedMessageId());
  }

  @Test
  public void toEntity_Should_Return_Null_If_MessageDTO_Is_Null() {
    assertNull(messageDtoService.toEntity(null));
  }

  @Test
  public void toEntity_Should_Return_Message() {
      when(userDAO.getById(1L)).thenReturn(user1);
      when(messageDAO.getById(2L)).thenReturn(sharedMessage);
      when(userDAO.getUsersByIds(new HashSet<>(Arrays.asList(1L, 2L))))
              .thenReturn(Arrays.asList(user1, user2));
      MessageDTO messageDTO = new MessageDTO(message);
      messageDTO.setUserId(1L);
      messageDTO.setSharedMessageId(2L);
      // TODO: Переделать
//    messageDTO.setRecipientUserIds(new HashSet<>(Arrays.asList(1L, 2L)));

      Message msg = messageDtoService.toEntity(messageDTO);
      assertEquals(3L, (long) msg.getId());
      assertEquals(user1, msg.getUser());
      assertEquals("Message 1", msg.getContent());
      assertEquals(localDateTime, msg.getDateCreate());
      assertFalse(msg.getIsDeleted());
      assertEquals(1L, (long) msg.getChannelId());
      assertEquals(sharedMessage, msg.getSharedMessage());
      assertTrue(msg.getRecipientUsers()
                         .contains(user1));
    assertTrue(msg.getRecipientUsers().contains(user2));
  }
}
