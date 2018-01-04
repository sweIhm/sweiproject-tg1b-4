package edu.hm.cs.iua;

import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    // test vars
    private Long userID;
    private String token;

    @Before
    public void setupRepositories() {
        // reset Repositories
        userRepository.deleteAll();
        tokenRepository.deleteAll();
        // add test user
        final IUAUser user = new IUAUser("TestUser", "test@test.test", "test", "CODE");
        user.setValidated(true);
        userID = userRepository.save(user).getId();
        // add test token
        token = "TOKEN";
        tokenRepository.save(new Token(userID, token));
    }

    @After
    public void clearRepositories() {
        // check that user repository didn't change
        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
        // check that token repository didn't change
        Assert.assertEquals(1, tokenRepository.count());
        for (Token token: tokenRepository.findAll())
            Assert.assertEquals("TOKEN", token.getKey());
        // reset Repositories
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void getUserTest() throws Exception {
        mockMvc.perform(get("/user/" + userID))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"name\":\"TestUser\"}"));
    }

    @Test
    public void getUserNotFoundTest() throws Exception {
        mockMvc.perform(get("/user/" + userID + 1))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"name\":\"TestUser\"}]"));
    }

}
