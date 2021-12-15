package com.zacharywarunek.amazonclone.registration;

import com.zacharywarunek.amazonclone.account.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
class RegistrationControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    RegistrationService registrationService;
    @Test
    void register() {
    }

    @Test
    void confirm() {
    }
}
