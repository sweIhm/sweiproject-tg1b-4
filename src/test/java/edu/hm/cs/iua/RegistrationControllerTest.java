package edu.hm.cs.iua;

import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @After
    public void clearRepository() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void createUserTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"CreationTestName\",\"email\":\"information.iua@hm.edu\",\"password\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());

        IUAUser user = userRepository.find("CreationTestName");
        long userId = user.getId();

        user.setValidated(true);
        userRepository.save(user);

        mockMvc.perform(get("/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"name\":\"CreationTestName\"}"));
    }

    // invalid name tests
    @Test
    public void invalidName_empty() throws Exception {
        final String username = "";

        mockMvc.perform(post("/register")
                .content("{\"name\":\"" + username + "\",\"email\":\"information.iua@hm.edu\",\"password\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidName_null() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"email\":\"information.iua@hm.edu\",\"password\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    // invalid email tests
    @Test
    public void invalidEmail_notValid() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"CreationTestName\",\"email\":\"information.iua@gmail.com\",\"password\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidEmail_null() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"CreationTestName\",\"password\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    // invalid password test
    @Test
    public void invalidPassword_null() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"CreationTestName\",\"email\":\"information.iua@gmail.com\"}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

    // activation test
    @Test
    public void activateUserTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"ActivateTestName\",\"email\":\"information.iua@hm.edu\",\"password\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());

        IUAUser user = userRepository.find("ActivateTestName");
        long userId = user.getId();
        String confirmationCode = user.getConfirmationCode();

        mockMvc.perform(get("/register" + "?userId=" + userId + "&code=" + confirmationCode))
                .andExpect(status().isOk());
    }

}
