package jm.controller.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jm.UserService;
import jm.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@RunWith(SpringRunner.class)
@WebAppConfiguration
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserRestControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService mockService;

    @Before
    public void init() {
        User user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setLastName("Last_Name");
        user.setLogin("Login");
        user.setPassword("Password");
        user.setEmail("Email");

        when(mockService.getUserById(1L)).thenReturn(user);
        //   .thenReturn(Optional.of(user));
    }

    @Test
    public void getUserById_OK() throws Exception {

        mockMvc.perform(get("/restapi/users/user/1"))
                /*.andDo(print())*/
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1L)))
                .andExpect(jsonPath("$.name", is("Name")))
                .andExpect(jsonPath("$.last_name", is("Last_Name")))
                .andExpect(jsonPath("$.login", is("Login")))
                .andExpect(jsonPath("$.password", is("Password")))
                .andExpect(jsonPath("$.email", is("Email")));
        verify(mockService, times(1)).getUserById(1L);

    }

}

