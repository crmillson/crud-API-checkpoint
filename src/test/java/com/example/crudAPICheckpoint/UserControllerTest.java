package com.example.crudAPICheckpoint;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import javax.transaction.Transactional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository repository;

    User user1, user2, user3, user4;

    @BeforeEach
    public void setup() {
        user1 = new User();
        user2 = new User();
        user3 = new User();
        user4 = new User();

        user1.setEmail("Stuff.things@email.com");
        user1.setPassword("12345");
        user2.setEmail("bob.dole@email.com");
        user2.setPassword("54321");
        user3.setEmail("ricky.bobby@email.com");
        user3.setPassword("6789");
        user4.setEmail("obama.trump@email.com");
        user4.setPassword("11111");


        this.repository.save(user1);
        this.repository.save(user2);
        this.repository.save(user3);
        this.repository.save(user4);

    }

    @Test
    @Transactional
    @Rollback
    public void getAllReturnsListOfUsers() throws Exception {

        MockHttpServletRequestBuilder request = get("/users");

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(user1.getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void addUserReturnsAllButPassword() throws Exception{
        String json = """
                {
                    "email": "john.example@email.com",
                    "password": "5555"
                }
                """;

        MockHttpServletRequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john.example@email.com"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdReturnsUserWithNoPassword() throws Exception {
        MockHttpServletRequestBuilder request = get(String.format("/users/%d", user1.getId()))
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user1.getEmail()));
    }

    @Test
    @Transactional
    @Rollback
    public void findByIdReturnsNotPresent() throws Exception {
        MockHttpServletRequestBuilder request = get("/users/7")
                .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Rollback
    public void updateUserByIdReturnsUpdatedUser() throws Exception {

        String emailAndPassJson = """
                {
                    "email": "bob.example@email.com",
                    "password": "4321"
                }
                """;
        String emailOnlyJson = """
                {
                    "email": "bob.example@email.com"
                }
                """;

        MockHttpServletRequestBuilder request = patch(String.format("/users/%d",user2.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(emailAndPassJson);
        MockHttpServletRequestBuilder request1 = patch(String.format("/users/%d",user2.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(emailOnlyJson);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob.example@email.com"));

        mvc.perform(request1)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("bob.example@email.com"));

        assertEquals("4321", user2.getPassword());




    }
}

