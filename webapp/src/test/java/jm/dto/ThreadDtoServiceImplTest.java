/*
package jm.dto;

import jm.model.Message;
import jm.model.ThreadChannel;
import jm.model.User;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.*;

public class ThreadDtoServiceImplTest {
  private ThreadDtoService threadDtoService;
  private ThreadChannel threadChannel;
  private LocalDateTime localDateTime = LocalDateTime.now();
  private User user1 = new User("Robert", "Martin", "uncle_bob", "bob.uncle@tdd.com", "1234");

  @Before
  public void setUp() throws Exception {
    user1.setId(1L);
    user1.setAvatarURL("image.jpg");
    Message message = new Message(3L, 1L, user1, "Message 1", localDateTime);

    threadChannel = new ThreadChannel(message);
    threadChannel.setId(4L);

    threadDtoService = new ThreadDtoServiceImpl();
  }

  @Test
  public void toDto() {
    ThreadDTO threadDTO = threadDtoService.toDto(threadChannel);
    assertEquals(3L, (long) threadDTO.getMessage().getId());
    assertEquals(1L, (long) threadDTO.getMessage().getUserId());
    assertEquals("Robert", threadDTO.getMessage().getUserName());
    assertEquals("image.jpg", threadDTO.getMessage().getUserAvatarUrl());
    assertEquals("Message 1", threadDTO.getMessage().getContent());
    assertEquals(4L, (long) threadDTO.getId());
  }
}*/
