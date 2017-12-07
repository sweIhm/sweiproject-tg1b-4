package edu.hm.cs.iua;

import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.models.User;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ActivityControllerTest {

    private static final Long USER_ID = (long)1;
    private static final Token TOKEN = new Token(USER_ID, "TEST_TOKEN");
    private static final String PARAM_STRING = "?user=" + USER_ID + "&token=" + TOKEN.getToken();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Before
    public void setTestUser() {
        if (!userRepository.exists(USER_ID)) {
            final User user = new User("Testuser", "test@test.test", "test", "CONFIRMATION_CODE");
            user.setValidated(true);
            userRepository.save(user);
        }
        if (!tokenRepository.exists(USER_ID))
            tokenRepository.save(TOKEN);
    }

    @Test
    public void listAllTest() throws Exception {
        mockMvc.perform(get("/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("[]"));
    }

    @Test
    public void createTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(
                post("/activity" + PARAM_STRING)
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(delete("/activity/" + id + PARAM_STRING));
    }

    @Test
    public void createMultipleTest() throws Exception {
        MockHttpServletResponse response1 = mockMvc.perform(post("/activity" + PARAM_STRING)
                .content("{\"title\":\"Test1\",\"text\":\"test test1\",\"tags\":\"test1\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        MockHttpServletResponse response2 = mockMvc.perform(post("/activity" + PARAM_STRING)
                .content("{\"title\":\"Test2\",\"text\":\"test test2\",\"tags\":\"test2\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        char id1 = response1.getContentAsString().charAt(6);
        char id2 = response2.getContentAsString().charAt(6);
        mockMvc.perform(delete("/activity/" + id1 + PARAM_STRING));
        mockMvc.perform(delete("/activity/" + id2 + PARAM_STRING));
    }

    @Test
    public void findTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity" + PARAM_STRING)
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(get("/activity/" + id))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/activity/" + id + PARAM_STRING));
    }

    @Test
    public void findWithUnUsedParamTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity" + PARAM_STRING)
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(get("/activity/" + id + PARAM_STRING))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/activity/" + id + PARAM_STRING));
    }

    @Test
    public void findTestFailed() throws Exception {
        mockMvc.perform(get("/activity/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void deleteTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity" + PARAM_STRING)
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(delete("/activity/" + id + PARAM_STRING))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity" + PARAM_STRING)
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(put("/activity/" + id + PARAM_STRING)
                .content("{\"title\":\"TestTest\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/activity/" + id + PARAM_STRING));
    }

    @Test
    public void updateTestFailed() throws Exception {
        mockMvc.perform(put("/activity/" + 0 + PARAM_STRING)
                .content("{\"title\":\"TestTest\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());
    }

}
