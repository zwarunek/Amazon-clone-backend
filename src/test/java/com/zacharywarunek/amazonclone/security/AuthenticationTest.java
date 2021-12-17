package com.zacharywarunek.amazonclone.security;

import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.config.Constants;
import com.zacharywarunek.amazonclone.config.JwtUtil;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class AuthenticationTest {

    @MockBean
    AccountRepo accountRepo;
    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private JwtUtil jwtUtil;

    @Test
    void shouldAuthenticate() throws Exception {
        Account account = new Account("Zach", "Warunek", "ZachAuth@gmail.com", "passwo234", AccountRole.ROLE_USER);
        given(accountRepo.findAccountByUsername(account.getUsername())).willReturn(java.util.Optional.of(account));

        mvc.perform(get("/apiTestWithAuth").accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + jwtUtil.generateToken(account)))
                .andExpect(status().isOk())
                .andExpect(content().string("API auth is functioning normally for environment: development"))
                .andReturn().getResponse();
    }

    @Test
    void authenticationUserNotFound() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "2wfg", AccountRole.ROLE_USER);
        String token = jwtUtil.generateToken(account);
        mvc.perform(put("/apiTestWithAuth").contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + token)).andExpect(status().is(401))
                .andExpect(status().reason("Unauthorized")).andReturn().getResponse();
    }

    @Test
    void authenticationTokenExpired() throws Exception {
        String expriredToken = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJ6QGdtYWlsLmNvbSIsInNjb3BlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjM5NjMxOTcwLCJleHAiOjE2Mzk2MzE5NzB9.xzjemMsq53NobQM5j3xZVY5s9s5z-1zT5aHFSNVuW9U";
        mvc.perform(put("/apiTestWithAuth" + 1).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + expriredToken))
                .andExpect(status().is(401)).andExpect(status().reason("Unauthorized")).andReturn().getResponse();
    }

    @Test
    void authenticationInvalidCombo() throws Exception {
        mvc.perform(put("/apiTestWithAuth" + 1).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + "randomString"))
                .andExpect(status().is(401)).andExpect(status().reason("Unauthorized")).andReturn().getResponse();
    }
}
