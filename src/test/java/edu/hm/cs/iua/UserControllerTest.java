package edu.hm.cs.iua;

import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import org.junit.Before;
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

    private static final Long USER_ID = (long)1;
    private static final Token TOKEN = new Token(USER_ID, "TEST_TOKEN");
    private static final String PARAM_STRING = "?user=" + USER_ID + "&token=" + TOKEN.getToken();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Before
    public void setTestUser() {
        if (!userRepository.exists(USER_ID)) {
            final IUAUser user = new IUAUser("Testuser", "test@test.test", "test", "CONFIRMATION_CODE");
            user.setValidated(true);
            userRepository.save(user);
        }
        if (!tokenRepository.exists(USER_ID))
            tokenRepository.save(TOKEN);
    }

    @Test
    public void checkId() throws Exception {
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"name\":\"Testuser\"}"));
    }
}
