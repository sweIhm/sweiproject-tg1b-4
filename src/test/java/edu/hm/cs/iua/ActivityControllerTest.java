package edu.hm.cs.iua;

import edu.hm.cs.iua.models.Activity;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.repositories.ActivityRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.repositories.IUAUserRepository;
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

    // environment vars
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    // test vars
    private Long userID;
    private String token;

    @Before
    public void setupRepositories() {
        // add test user
        final IUAUser user = new IUAUser("TestUser", "test@test.test", "test", "");
        user.setValidated(true);
        userID = userRepository.save(user).getId();
        // add test token
        token = "TOKEN";
        tokenRepository.save(new Token(userID, token));
    }

    @After
    public void clearRepositories() {
        // check that user repository didn't change
        final IUAUser wantUser = new IUAUser("TestUser", "test@test.test", "test", "");
        wantUser.setValidated(true);
        Assert.assertEquals(1, userRepository.count());
        for (IUAUser haveUser: userRepository.findAll()) {
            userID = haveUser.getId();
            wantUser.setId(userID);
            Assert.assertEquals(wantUser, haveUser);
        }
        // check that token repository didn't change
        final Token wantToken = new Token(userID, "TOKEN");
        Assert.assertEquals(1, tokenRepository.count());
        for (Token haveToken: tokenRepository.findAll())
            Assert.assertEquals(wantToken, haveToken);
        // reset Repositories
        activityRepository.deleteAll();
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void listAllEmptyTest() throws Exception {
        mockMvc.perform(
                get("/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("[]"));

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void listAllTest() throws Exception {
        activityRepository.save(new Activity(userID, "Title", "Text", "Tags"));
        Long id = 0L;
        for (Activity activity: activityRepository.findAll())
            id = activity.getId();

        mockMvc.perform(
                get("/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("[{\"id\":" + id + ",\"author\":" + userID +
                        ",\"text\":\"Text\",\"tags\":\"Tags\",\"title\":\"Title\"}]"));

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void createActivityTest() throws Exception {
        mockMvc.perform(
                post("/activity")
                        .param("user", userID.toString())
                        .param("token", token)
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());

        final Activity want = new Activity(userID, "Test", "test test", "test");

        for (Activity have: activityRepository.findAll()) {
            want.setId(have.getId());
            Assert.assertEquals(want, have);
        }
        Assert.assertEquals(activityRepository.count(), 1);
    }

    @Test
    public void createMultipleTest() throws Exception {
        mockMvc.perform(
                post("/activity")
                        .param("user", userID.toString())
                        .param("token", token)
                        .content("{\"title\":\"Test1\",\"text\":\"test test1\",\"tags\":\"test1\"}")
                        .contentType("application/json"))
                .andExpect(status().isOk());
        mockMvc.perform(
                post("/activity")
                        .param("user", userID.toString())
                        .param("token", token)
                        .content("{\"title\":\"Test2\",\"text\":\"test test2\",\"tags\":\"test2\"}")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Assert.assertEquals(2, activityRepository.count());
    }

    @Test
    public void findTest() throws Exception {
        final Long id = activityRepository.save(new Activity(userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                get("/activity/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\":" + id + ",\"author\":" + userID +
                        ",\"title\":\"Title\",\"text\":\"Text\",\"tags\":\"Tags\"}"));

        Assert.assertEquals(1, activityRepository.count());
    }

    @Test
    public void findTestFailed() throws Exception {
        mockMvc.perform(
                get("/activity/9999"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void deleteTest() throws Exception {
        final Long id = activityRepository.save(new Activity(userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                delete("/activity/" + id)
                        .param("user", userID.toString())
                        .param("token", token))
                .andExpect(status().isOk());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void updateTest() throws Exception {
        final Long id = activityRepository.save(new Activity(userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                put("/activity/" + id)
                        .param("user", userID.toString())
                        .param("token", token)
                .content("{\"title\":\"Test\",\"text\":\"test\",\"tags\":\"tag\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());

        final Activity want = new Activity(userID, "Test", "test", "tag");
        want.setId(id);
        final Activity have = activityRepository.findOne(id);

        Assert.assertEquals(1, activityRepository.count());
        Assert.assertEquals(want, have);
    }

    @Test
    public void updateTestFailed() throws Exception {
        mockMvc.perform(
                put("/activity/9999")
                        .param("user", userID.toString())
                        .param("token", token)
                .content("{\"title\":\"TestTest\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, activityRepository.count());
    }

}
