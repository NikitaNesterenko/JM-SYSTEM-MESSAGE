package jm.dto;

import jm.api.dao.BotDAO;
import jm.api.dao.ChannelDAO;
import jm.api.dao.MessageDAO;
import jm.api.dao.UserDAO;
import jm.model.Conversation;
import jm.model.User;
import jm.model.Workspace;
import jm.model.message.DirectMessage;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class DirectMessageDtoServiceImplTest {
  private DirectMessageDtoServiceImpl directMessageDtoService;
  private DirectMessage directMessage;
  private DirectMessageDTO directMessageDTO;
  private LocalDateTime localDateTime = LocalDateTime.now();
  private User user1 = new User("Robert", "Martin", "uncle_bob", "bob.uncle@tdd.com", "1234");
  private User user2 = new User("Joshua", "Bloch", "j_bloch", "j.bloch@java.com", "4321");
  private Workspace wks =
      new Workspace("test_wks", new HashSet<>(Arrays.asList(user1, user2)), user1, false, null);
  private Conversation conversation = new Conversation(user1, user2, wks, true, true);

  @Mock UserDAO userDAO;
  @Mock BotDAO botDAO;
  @Mock ChannelDAO channelDAO;
  @Mock MessageDAO messageDAO;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    directMessageDtoService = new DirectMessageDtoServiceImpl();
    directMessageDtoService.setDirectMessageDtoServiceImpl(userDAO, botDAO, channelDAO, messageDAO);

    user1.setId(1L);
    user2.setId(2L);
    wks.setId(1L);

    directMessage = new DirectMessage();
    directMessage.setConversation(conversation);
    directMessage.setRecipientUsers(new HashSet<>());
    directMessage.setId(1L);
    directMessage.setContent("test dm");
    directMessage.setUser(user1);
    directMessage.setDateCreate(localDateTime);
  }

  @Test
  public void toDto_Should_Return_Null_If_DM_Is_Null() {
    assertNull(directMessageDtoService.toDto((DirectMessage) null));
  }

  @Test
  public void toDto_Should_Return_Direct_Message_Dto() {
    DirectMessageDTO directMessageDTO = directMessageDtoService.toDto(directMessage);

    assertEquals(user1.getId(), directMessageDTO.getUserId());
    assertEquals(user1.getName(), directMessageDTO.getUserName());
    assertNull(directMessageDTO.getChannelName());
    assertNull(directMessageDTO.getSharedMessageId());
    assertEquals(0, directMessageDTO.getRecipientUserIds().size());
    assertNull(directMessageDTO.getParentMessageId());
    assertEquals("test dm", directMessageDTO.getContent());
    assertEquals(localDateTime, directMessageDTO.getDateCreate());
    assertEquals(conversation, directMessageDTO.getConversation());
  }

  @Test
  public void toEntity_Should_Return_Null_If_DirectMessageDTO_Is_Null() {
    assertNull(directMessageDtoService.toEntity(directMessageDTO));
  }

  @Test
  public void toEntity_Should_Return_Direct_Message() {
    when(userDAO.getById(1L)).thenReturn(user1);
    when(userDAO.getUsersByIds(null)).thenReturn(Collections.emptyList());

    DirectMessageDTO directMessageDTO = new DirectMessageDTO();
    directMessageDTO.setConversation(conversation);
    directMessageDTO.setContent("test dm entity");
    directMessageDTO.setUserId(1L);
    directMessageDTO.setDateCreate(localDateTime);
    directMessageDTO.setIsDeleted(false);

    DirectMessage dm = directMessageDtoService.toEntity(directMessageDTO);

    assertEquals(conversation, dm.getConversation());
    assertEquals("test dm entity", dm.getContent());
    assertEquals(user1, dm.getUser());
    assertEquals(localDateTime, dm.getDateCreate());
    assertFalse(dm.getIsDeleted());
    assertEquals(0, dm.getRecipientUsers().size());
  }

  @Test
  public void toDto_Should_Return_Empty_List_If_DM_List_Is_Empty_Or_Null() {
    List<DirectMessageDTO> dmList = directMessageDtoService.toDto(new ArrayList<>());
    assertEquals(0, dmList.size());

    dmList = directMessageDtoService.toDto((List<DirectMessage>) null);
    assertEquals(0, dmList.size());
  }

  @Test
  public void toDto_Should_Return_List_With_DirectMessageDTOs() {
    List<DirectMessageDTO> directMessageDTOList = directMessageDtoService.toDto(Collections.singletonList(directMessage));

    assertEquals(user1.getId(), directMessageDTOList.get(0).getUserId());
    assertEquals(user1.getName(), directMessageDTOList.get(0).getUserName());
    assertNull(directMessageDTOList.get(0).getChannelName());
    assertNull(directMessageDTOList.get(0).getSharedMessageId());
    assertEquals(0, directMessageDTOList.get(0).getRecipientUserIds().size());
    assertNull(directMessageDTOList.get(0).getParentMessageId());
    assertEquals("test dm", directMessageDTOList.get(0).getContent());
    assertEquals(localDateTime, directMessageDTOList.get(0).getDateCreate());
    assertEquals(conversation, directMessageDTOList.get(0).getConversation());
  }
}
