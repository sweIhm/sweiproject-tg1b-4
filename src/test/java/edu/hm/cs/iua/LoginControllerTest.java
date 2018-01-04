package edu.hm.cs.iua;

import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    // test vars
    private Long userID;

    @Before
    public void setupRepository() {
        // reset Repositories
        userRepository.deleteAll();
        tokenRepository.deleteAll();
        // add test user
        final IUAUser user = new IUAUser("TestUser", "test@test.test", "test", "CODE");
        user.setValidated(true);
        userID = userRepository.save(user).getId();
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
        // reset Repositories
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void loginUserTest() throws Exception {
        mockMvc.perform(
                get("/login")
                        .param("email", "test@test.test")
                        .param("password", "test"))
                .andExpect(status().isOk())
                .andExpect(mvcResult -> {
                    Assert.assertEquals(1, tokenRepository.count());
                    for (Token token: tokenRepository.findAll())
                        Assert.assertEquals(token.getId(), userID);
                })
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{" +
                        "\"id\":" + userID + "," +
                        "\"key\":\"" + tokenRepository.findOne(userID).getKey() + "\"" +
                        "}"));
    }

    @Test
    public void loginLoggedInUserTest() throws Exception {
        tokenRepository.save(new Token(userID, "TOKEN"));
        mockMvc.perform(
                get("/login")
                        .param("email", "test@test.test")
                        .param("password", "test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\":" + userID + ",\"key\":\"TOKEN\"}"));
        Assert.assertEquals(1, tokenRepository.count());
        for (Token token: tokenRepository.findAll()) {
            Assert.assertEquals(userID, token.getId());
            Assert.assertEquals("TOKEN", token.getKey());
        }
    }

    @Test
    public void loginInvalidPasswordTest() throws Exception {
        mockMvc.perform(
                get("/login")
                        .param("email", "test@test.test")
                        .param("password", "invalid"))
                .andExpect(status().isBadRequest());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void loginInvalidEmailTest() throws Exception {
        mockMvc.perform(
                get("/login")
                        .param("email", "invalid@test.test")
                        .param("password", "test"))
                .andExpect(status().isBadRequest());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void loginNotValidatedUserTest() throws Exception {
        // create unvalidated user
        final Long user2Id = userRepository.save(new IUAUser("TestUser2", "test2@test.test", "test", "VALIDATION_CODE")).getId();
        // actual test
        mockMvc.perform(
                get("/login")
                        .param("email", "test2@test.test")
                        .param("password", "test"))
                .andExpect(status().isBadRequest());
        Assert.assertEquals(0, tokenRepository.count());
        // delete unvalidated user
        userRepository.delete(user2Id);
    }
}
