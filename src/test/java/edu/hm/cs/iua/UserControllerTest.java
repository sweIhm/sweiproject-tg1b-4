package edu.hm.cs.iua;

import edu.hm.cs.iua.models.Activity;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
                .andExpect(content().string("{\"id\":" + userID + ",\"name\":\"TestUser\"}"));

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void getUserNotFoundTest() throws Exception {
        mockMvc.perform(get("/user/9999"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void getUserNotValidatedTest() throws Exception {
        // set test user to unvalidated
        final IUAUser user = userRepository.findOne(userID);
        user.setValidated(false);
        userRepository.save(user);

        mockMvc.perform(get("/user/" + userID))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        mockMvc.perform(get("/user"))
                .andExpect(status().isOk())
                .andExpect(content().string("[{\"id\":" + userID + ",\"name\":\"TestUser\"}]"));

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void updateUserNameTest() throws Exception {
        mockMvc.perform(put("/user/" + userID)
                .param("user", userID.toString())
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Testing\"}"))
                .andExpect(status().isOk());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("Testing", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void updateUserPasswordTest() throws Exception {
        mockMvc.perform(put("/user/" + userID)
                .param("user", userID.toString())
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"testing\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isOk());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("testing", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void updateUserNameAndPasswordTest() throws Exception {
        mockMvc.perform(put("/user/" + userID)
                .param("user", userID.toString())
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Testing\",\"password\":\"testing\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isOk());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("Testing", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("testing", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void updateOtherUserTest() throws Exception {
        mockMvc.perform(put("/user/9999")
                .param("user", userID.toString())
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Testing\"}"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void updateUserNameEmptyTest() throws Exception {
        mockMvc.perform(put("/user/" + userID)
                .param("user", userID.toString())
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void updateUserPasswordEmptyTest() throws Exception {
        mockMvc.perform(put("/user/" + userID)
                .param("user", userID.toString())
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void updateUserPasswordInvalidConfirmationCodeTest() throws Exception {
        mockMvc.perform(put("/user/" + userID)
                .param("user", userID.toString())
                .param("token", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"password\":\"testing\",\"confirmationCode\":\"invalid\"}"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals(userID, user.getId());
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@test.test", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void getDefaultPictureTest() throws Exception {
        mockMvc.perform(get("/user/" + userID + "/picture"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(Files.readAllBytes(Paths.get(new ClassPathResource("user_default_picture.png").getURI()))));
    }

    @Test
    public void uploadPictureTest() throws Exception {
        mockMvc.perform(fileUpload("/user/" + userID + "/picture")
                .file(new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE,
                        Files.readAllBytes(Paths.get(new ClassPathResource("activity_default_picture.png").getURI()))))
                .param("user", userID.toString())
                .param("token", token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_PNG))
                .andExpect(content().bytes(Files.readAllBytes(Paths.get(new ClassPathResource("activity_default_picture.png").getURI()))));

        Assert.assertTrue(Paths.get("data/user_" + userID + ".png").toFile().exists());
    }

    @Test
    public void uploadPictureFromOtherUserTest() throws Exception {
        mockMvc.perform(fileUpload("/user/9999/picture")
                .file(new MockMultipartFile("file", "test.png", MediaType.IMAGE_PNG_VALUE,
                        Files.readAllBytes(Paths.get(new ClassPathResource("activity_default_picture.png").getURI()))))
                .param("user", userID.toString())
                .param("token", token))
                .andExpect(status().isUnauthorized());
    }

}
