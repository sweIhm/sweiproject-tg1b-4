package edu.hm.cs.iua;

import edu.hm.cs.iua.controllers.RegistrationController;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
    @Autowired
    private ApplicationContext context;

    @After
    public void clearRepository() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void registerUserInvalidEmailTest1() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserInvalidEmailTest2() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserInvalidEmailTest3() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"invalid\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserInvalidEmailTest4() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"invalid@invalid@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserInvalidEmailTest5() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"invalid@\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserInvalidEmailTest6() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"invalid@test.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserNullEmailTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserInvalidNameTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"\",\"email\":\"test@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUseNullNameTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"email\":\"test@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserInvalidPasswordTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"test@hm.edu\",\"password\":\"\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserNullPasswordTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"test@hm.edu\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserEmailTakenTest() throws Exception {
        userRepository.save(new IUAUser("Tester", "test@hm.edu", "password", "CODE"));
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"test@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("Tester", user.getName());
            Assert.assertEquals("test@hm.edu", user.getEmail());
            Assert.assertEquals("password", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(false, user.isValidated());
        }
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserNameTakenTest() throws Exception {
        userRepository.save(new IUAUser("Tester", "test@hm.edu", "password", "CODE"));
        mockMvc.perform(post("/register")
                .content("{\"name\":\"Tester\",\"email\":\"test2@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("Tester", user.getName());
            Assert.assertEquals("test@hm.edu", user.getEmail());
            Assert.assertEquals("password", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(false, user.isValidated());
        }
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"test@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@hm.edu", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals(false, user.isValidated());
        }
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void registerUserWithExistingUserTest() throws Exception {
        userRepository.save(new IUAUser("Tester", "tester@hm.edu", "test", "CODE"));

        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"test@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        Assert.assertEquals(2, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
    }

    @Test
    public void activateUserTest() throws Exception {
        final Long userID = userRepository.save(new IUAUser("TestUser", "test@hm.edu", "test", "CODE")).getId();

        mockMvc.perform(get("/register")
                    .param("userId", userID.toString())
                    .param("code", "CODE"))
                .andExpect(status().isOk())
                .andExpect(view().name("activationSuccessful"));

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@hm.edu", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals(null, user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
        }
    }

    @Test
    public void activateUserNotFoundTest() throws Exception {
        final Long userID = userRepository.save(new IUAUser("TestUser", "test@hm.edu", "test", "CODE")).getId();

        mockMvc.perform(get("/register")
                .param("userId", "9999")
                .param("code", "CODE"))
                .andExpect(status().isOk())
                .andExpect(view().name("activationUserNotFound"));

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@hm.edu", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(false, user.isValidated());
        }
    }

    @Test
    public void activateUserInvalidCodeTest() throws Exception {
        final Long userID = userRepository.save(new IUAUser("TestUser", "test@hm.edu", "test", "CODE")).getId();

        mockMvc.perform(get("/register")
                .param("userId", userID.toString())
                .param("code", "INVALID"))
                .andExpect(status().isOk())
                .andExpect(view().name("activationInvalidCode"));

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("test@hm.edu", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(false, user.isValidated());
        }
    }

    @Test
    public void emailTransmissionFailedTest() throws Exception {
        // Let's do something evil, so we can test for the EmailTransmissionFailed exception
        final RegistrationController controller = context.getBean(RegistrationController.class);
        final Field field = controller.getClass().getDeclaredField("emailServer");
        field.setAccessible(true);
        // store the old value, because we will need it later
        final Object oldValue = field.get(controller);
        field.set(controller, "");

        // Now our test will fail, because we removed the email server address
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"test@hm.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());

        // Finally we make the evil shit undone
        field.set(controller, oldValue);
        field.setAccessible(false);
    }

}
