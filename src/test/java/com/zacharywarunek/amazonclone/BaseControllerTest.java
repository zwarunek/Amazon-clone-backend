package com.zacharywarunek.amazonclone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zacharywarunek.amazonclone.account.Account;
import com.zacharywarunek.amazonclone.account.AccountRepo;
import com.zacharywarunek.amazonclone.account.AccountRole;
import com.zacharywarunek.amazonclone.config.Constants;
import com.zacharywarunek.amazonclone.config.JwtUtil;
import com.zacharywarunek.amazonclone.util.AuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class BaseControllerTest {


    @MockBean
    AccountRepo accountRepo;
    @Autowired
    private MockMvc mvc;
    @InjectMocks
    private JwtUtil jwtUtil;
    @InjectMocks
    private BCryptPasswordEncoder passwordEncoder;

    public static String asJsonString(final Object obj) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void apiTest() throws Exception {

        mvc.perform(get("/api/v1/apiTest").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("API is functioning normally for environment: development"))
                .andReturn().getResponse();
    }

    @Test
    void shouldAuthenticate() throws Exception {
        Account account = new Account("Zach", "Warunek", "ZachAuth@gmail.com", "passwo234", AccountRole.ROLE_USER);
        given(accountRepo.findAccountByUsername(account.getUsername())).willReturn(java.util.Optional.of(account));

        mvc.perform(get("/api/v1/apiTestAuth").accept(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + jwtUtil.generateToken(account)))
                .andExpect(status().isOk())
                .andExpect(content().string("API Auth is functioning normally for environment: development"))
                .andReturn().getResponse();
    }

    @Test
    void authenticationUserNotFound() throws Exception {
        Account account = new Account("Zach", "Warunek", "Zach@gmail.com", "2wfg", AccountRole.ROLE_USER);
        String token = jwtUtil.generateToken(account);
        given(accountRepo.findAccountByUsername(account.getUsername())).willReturn(java.util.Optional.empty());
        mvc.perform(get("/api/v1/apiTestAuth").contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + token)).andExpect(status().is(404))
                .andExpect(status().reason("Account with username " + account.getUsername() + " doesn't exist")).andReturn().getResponse();
    }

    @Test
    void authenticationTokenExpired() throws Exception {
        String expriredToken = "eyJhbGciOiJIUzI1NiJ9" +
                ".eyJzdWIiOiJ6QGdtYWlsLmNvbSIsInNjb3BlcyI6W3siYXV0aG9yaXR5IjoiUk9MRV9BRE1JTiJ9XSwiaWF0IjoxNjM5NjMxOTcwLCJleHAiOjE2Mzk2MzE5NzB9.xzjemMsq53NobQM5j3xZVY5s9s5z-1zT5aHFSNVuW9U";
        mvc.perform(put("/api/v1/apiTestAuth" + 1).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + expriredToken))
                .andExpect(status().is(401)).andExpect(status().reason("Unauthorized")).andReturn().getResponse();
    }

    @Test
    void authenticationInvalidCombo() throws Exception {
        mvc.perform(put("/api/v1/apiTestAuth" + 1).contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", Constants.TOKEN_PREFIX + "randomString"))
                .andExpect(status().is(401)).andExpect(status().reason("Unauthorized")).andReturn().getResponse();
    }

    @Test
    void authenticate() throws Exception {

        String password = "password1234";
        Account account = new Account("Zach",
                                      "Warunek",
                                      "Zach@gmail.com",
                                      passwordEncoder.encode(password),
                                      AccountRole.ROLE_USER);
        AuthRequest authRequest = new AuthRequest("Zach@gmail.com", password);
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.of
        (account));

        mvc.perform(post("/api/v1/authenticate").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(authRequest))).andExpect(status().isOk())
                .andExpect(content().string("Authorization Successful"));
    }

    @Test
    void authenticateUsernamePasswordDontMatch() throws Exception {

        AuthRequest authRequest = new AuthRequest("NotInDB@gmail.com", "password1234");
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.empty());

        mvc.perform(post("/api/v1/authenticate").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(authRequest))).andExpect(status().is(HttpStatus.UNAUTHORIZED
                            .value()))
                .andExpect(status().reason("Username or Password was incorrect"));
    }

    @Test
    void authenticateUsernameNotFound() throws Exception {

        AuthRequest authRequest = new AuthRequest(null, "password1234");
        given(accountRepo.findAccountByUsername(authRequest.getUsername())).willReturn(java.util.Optional.empty());

        mvc.perform(post("/api/v1/authenticate").contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(authRequest))).andExpect(status().is(HttpStatus.BAD_REQUEST.value
                            ()))
                .andExpect(status().reason("'username' or 'password' fields not found"));
    }
}
