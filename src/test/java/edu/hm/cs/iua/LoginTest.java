package edu.hm.cs.iua;

import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Test
    public void loginUserTest() throws Exception {
        final String userName = "LoginTestName";
        final String email = "information.iua@hm.edu";
        final String password = "test";

//        mockMvc.perform(post("/register")
//                .content("{\"name\":\"" + userName + "\",\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")
//                .contentType("application/json"))
//                .andExpect(status().isOk());
        final IUAUser presetUser = new IUAUser(userName, email, password, "CONFIRMATION_CODE");
        userRepository.save(presetUser);

        IUAUser user = userRepository.find(userName);

        long userId = user.getId();
        String confirmationCode = user.getConfirmationCode();
        mockMvc.perform(get("/register" + "?userId=" + userId + "&code=" + confirmationCode))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login" + "?email=" + email + "&password=" + password))
                .andExpect(status().isOk());

        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }
}
