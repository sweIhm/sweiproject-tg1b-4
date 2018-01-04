package edu.hm.cs.iua;

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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    public void registerUserInvalidEmailTest() throws Exception {
        mockMvc.perform(post("/register")
                .content("{\"name\":\"TestUser\",\"email\":\"invalid@test.edu\",\"password\":\"test\"}")
                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, userRepository.count());
        Assert.assertEquals(0, tokenRepository.count());
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

}
