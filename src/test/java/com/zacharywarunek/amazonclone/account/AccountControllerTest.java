package com.zacharywarunek.amazonclone.account;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.MockMvcConfigurer.*;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @MockBean
    AccountService accountService;
    @Autowired
    private MockMvc mvc;


    @Test
    void shouldCreateMockMvc() {
        assertNotNull(mvc);
    }

    @Test
    @WithMockUser(username = "test@gmail.com", roles = {"USER"})
    void shouldGetAllAccounts() throws Exception {
        Account account1 = new Account(
                "Zach",
                "Warunek",
                "Zach@gmail.com",
                "password1234",
                AccountRole.USER
        );
        Account account2 = new Account(
                "Zach2",
                "Warunek2",
                "Zach@gmail.com2",
                "password12342",
                AccountRole.USER
        );
        given(accountService.getAllAccounts()).willReturn(Arrays.asList(account1, account2));
        this.mvc.perform(get("/api/v1/account")).andExpect(status().isOk())
                .andExpect(jsonPath("$[0].first_name").value(account1.getFirst_name()))
                .andExpect(jsonPath("$[0].last_name").value(account1.getLast_name()))
                .andExpect(jsonPath("$[0].password").value(account1.getPassword()))
                .andExpect(jsonPath("$[0].username").value(account1.getUsername()))
                .andExpect(jsonPath("$[1].first_name").value(account2.getFirst_name()))
                .andExpect(jsonPath("$[1].last_name").value(account2.getLast_name()))
                .andExpect(jsonPath("$[1].password").value(account2.getPassword()))
                .andExpect(jsonPath("$[1].username").value(account2.getUsername()));
    }

    @Test
    void shouldAuthenticate() {

    }

    @Test
    void updateAccount() {
    }

    @Test
    void deleteAccount() {
    }
}
