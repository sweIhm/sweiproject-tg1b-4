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

        mockMvc.perform(get("/register/activate")
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

        mockMvc.perform(get("/register/activate")
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

        mockMvc.perform(get("/register/activate")
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

    @Test
    public void registerUserFallBackMail() throws Exception {
        // Let's do a bit more evil stuff, so we can test the confirmation email fallback
        final Field field = RegistrationController.class.getDeclaredField("confirmationEmail");
        field.setAccessible(true);
        // store the old value, because we will need it later
        final Object oldValue = field.get(RegistrationController.class);
        field.set(null, "/not/found");

        // Now we register a user and expect everything to be fine
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

        // Finally we make the evil shit undone
        field.set(RegistrationController.class, oldValue);
        field.setAccessible(false);
}

    @Test
    public void activateValidatedUserTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "test@hm.edu", "test", null);
        testUser.setValidated(true);
        final Long userID = userRepository.save(testUser).getId();

        mockMvc.perform(get("/register/activate")
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
    public void requestPasswordResetTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", null);
        testUser.setValidated(true);
        final Long userId = userRepository.save(testUser).getId();

        mockMvc.perform(get("/register/request_reset")
                .param("email", "information.iua@gmail.com"))
                .andExpect(status().isOk());
    }

    @Test
    public void resetPageTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE");
        testUser.setValidated(true);
        testUser.setRequestingPassword(true);
        final Long userId = userRepository.save(testUser).getId();

        mockMvc.perform(get("/register/reset")
                .param("userId", userId.toString())
                .param("code", "CODE"))
                .andExpect(status().isOk())
                .andExpect(view().name("resetPasswordPage"));
    }

    @Test
    public void resetPasswordTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE");
        testUser.setValidated(true);
        testUser.setRequestingPassword(true);
        final Long userId = userRepository.save(testUser).getId();

        mockMvc.perform(post("/register/reset")
                .param("userId", userId.toString())
                .param("code", "CODE").contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"new_test\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isOk());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("information.iua@gmail.com", user.getEmail());
            Assert.assertEquals("new_test", user.getPassword());
            Assert.assertEquals(null, user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
            Assert.assertEquals(false, user.isRequestingPassword());
        }
    }

    @Test
    public void resetPasswordUserNotFoundTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE");
        testUser.setValidated(true);
        testUser.setRequestingPassword(true);
        userRepository.save(testUser);

        mockMvc.perform(post("/register/reset")
                .param("userId", "9999")
                .param("code", "CODE").contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"new_test\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("information.iua@gmail.com", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
            Assert.assertEquals(true, user.isRequestingPassword());
        }
    }

    @Test
    public void resetPasswordUserNotRequestingPasswordTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE");
        testUser.setValidated(true);
        final Long userId = userRepository.save(testUser).getId();

        mockMvc.perform(post("/register/reset")
                .param("userId", userId.toString())
                .param("code", "CODE").contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"new_test\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("information.iua@gmail.com", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
            Assert.assertEquals(false, user.isRequestingPassword());
        }
    }

    @Test
    public void resetPasswordInvalidCodeTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE");
        testUser.setValidated(true);
        testUser.setRequestingPassword(true);
        final Long userId = userRepository.save(testUser).getId();

        mockMvc.perform(post("/register/reset")
                .param("userId", userId.toString())
                .param("code", "INVALID").contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"new_test\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("information.iua@gmail.com", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
            Assert.assertEquals(true, user.isRequestingPassword());
        }
    }

    @Test
    public void resetPasswordInvalidNullTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE");
        testUser.setValidated(true);
        testUser.setRequestingPassword(true);
        final Long userId = userRepository.save(testUser).getId();

        mockMvc.perform(post("/register/reset")
                .param("userId", userId.toString())
                .param("code", "CODE").contentType(MediaType.APPLICATION_JSON).content("{\"confirmationCode\":\"test\"}"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("information.iua@gmail.com", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
            Assert.assertEquals(true, user.isRequestingPassword());
        }
    }

    @Test
    public void resetPasswordInvalidEmptyTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE");
        testUser.setValidated(true);
        testUser.setRequestingPassword(true);
        final Long userId = userRepository.save(testUser).getId();

        mockMvc.perform(post("/register/reset")
                .param("userId", userId.toString())
                .param("code", "CODE").contentType(MediaType.APPLICATION_JSON).content("{\"password\":\"\",\"confirmationCode\":\"test\"}"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(1, userRepository.count());
        for (IUAUser user: userRepository.findAll()) {
            Assert.assertEquals("TestUser", user.getName());
            Assert.assertEquals("information.iua@gmail.com", user.getEmail());
            Assert.assertEquals("test", user.getPassword());
            Assert.assertEquals("CODE", user.getConfirmationCode());
            Assert.assertEquals(true, user.isValidated());
            Assert.assertEquals(true, user.isRequestingPassword());
        }
    }

    @Test
    public void requestPasswordResetFallBackMailTest() throws Exception {
        // Again some black magic, so we can test the reset password email fallback
        final Field field = RegistrationController.class.getDeclaredField("resetPassEmail");
        field.setAccessible(true);
        // store the old value, because we will need it later
        final Object oldValue = field.get(RegistrationController.class);
        field.set(null, "/not/found");


        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", null);
        testUser.setValidated(true);
        userRepository.save(testUser);

        mockMvc.perform(get("/register/request_reset")
                .param("email", "information.iua@gmail.com"))
                .andExpect(status().isOk());

        // Finally we make the evil shit undone (again)
        field.set(RegistrationController.class, oldValue);
        field.setAccessible(false);
    }

    @Test
    public void requestPasswordResetUnvalidatedUserTest() throws Exception {
        userRepository.save(new IUAUser("TestUser", "information.iua@gmail.com", "test", "CODE"));
        mockMvc.perform(get("/register/request_reset")
                .param("email", "information.iua@gmail.com"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestPasswordResetUserNotFoundTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "information.iua@gmail.com", "test", null);
        testUser.setValidated(true);
        userRepository.save(testUser);

        mockMvc.perform(get("/register/request_reset")
                .param("email", "test@hm.edu"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void requestPasswordEmailTransmissionFailedTest() throws Exception {
        final IUAUser testUser = new IUAUser("TestUser", "invalid", "test", null);
        testUser.setValidated(true);
        userRepository.save(testUser);

        mockMvc.perform(get("/register/request_reset")
                .param("email", "invalid"))
                .andExpect(status().isBadRequest());
    }

}
