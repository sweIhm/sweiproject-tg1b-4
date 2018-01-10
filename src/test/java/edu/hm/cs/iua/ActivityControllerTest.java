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
        activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tag1", "Tag2"));
        Long id = 0L;
        for (Activity activity: activityRepository.findAll())
            id = activity.getId();

        mockMvc.perform(
                get("/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("[{\"id\":" + id + ",\"author\":" + userID + ",\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}]"));

        Assert.assertEquals(1, activityRepository.count());
    }

    @Test
    public void createActivityTest() throws Exception {
        mockMvc.perform(
                post("/activity")
                        .param("user", userID.toString())
                        .param("token", token)
                        .content("{\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Assert.assertEquals(1, activityRepository.count());
        for (Activity activity: activityRepository.findAll()) {
            Assert.assertEquals("Title", activity.getTitle());
            Assert.assertEquals("Text", activity.getText());
            String[] want = {"Tag1", "Tag2"};
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertEquals(new Integer(27), activity.getDay());
            Assert.assertEquals(new Integer(1), activity.getMonth());
            Assert.assertEquals(new Integer(2004), activity.getYear());
            Assert.assertEquals(new Integer(16), activity.getCapacity());
        }
    }

    @Test
    public void createActivityTestInvalidUserID() throws Exception {
        mockMvc.perform(
                post("/activity")
                        .param("user", "9999")
                        .param("token", token)
                        .content("{\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void createActivityTestInvalidToken() throws Exception {
        mockMvc.perform(
                post("/activity")
                        .param("user", userID.toString())
                        .param("token", "INVALID_TOKEN")
                        .content("{\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void createMultipleTest() throws Exception {
        mockMvc.perform(
                post("/activity")
                        .param("user", userID.toString())
                        .param("token", token)
                        .content("{\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title1\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}")
                        .contentType("application/json"))
                .andExpect(status().isOk());
        mockMvc.perform(
                post("/activity")
                        .param("user", userID.toString())
                        .param("token", token)
                        .content("{\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title2\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}")
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Assert.assertEquals(2, activityRepository.count());
    }

    @Test
    public void findTest() throws Exception {
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tag1", "Tag2")).getId();

        mockMvc.perform(
                get("/activity/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().json("{\"id\":" + id + ",\"author\":" + userID + ",\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}"));

        Assert.assertEquals(1, activityRepository.count());
    }

    @Test
    public void findTestActivityNotFound() throws Exception {
        mockMvc.perform(
                get("/activity/9999"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void deleteTest() throws Exception {
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                delete("/activity/" + id)
                        .param("user", userID.toString())
                        .param("token", token))
                .andExpect(status().isOk());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void deleteTestActivityNotFound() throws Exception {
        mockMvc.perform(
                delete("/activity/9999")
                        .param("user", userID.toString())
                        .param("token", token))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void deleteTestInvalidUserId() throws Exception {
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                delete("/activity/" + id)
                        .param("user", "9999")
                        .param("token", token))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, activityRepository.count());
        for (Activity activity: activityRepository.findAll()) {
            Assert.assertEquals("Title", activity.getTitle());
            Assert.assertEquals("Text", activity.getText());
            String[] want = {"Tags"};
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertEquals(new Integer(27), activity.getDay());
            Assert.assertEquals(new Integer(1), activity.getMonth());
            Assert.assertEquals(new Integer(2004), activity.getYear());
            Assert.assertEquals(new Integer(16), activity.getCapacity());
        }
    }

    @Test
    public void deleteTestInvalidToken() throws Exception {
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                delete("/activity/" + id)
                        .param("user", userID.toString())
                        .param("token", "INVALID_TOKEN"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, activityRepository.count());
        for (Activity activity: activityRepository.findAll()) {
            Assert.assertEquals("Title", activity.getTitle());
            Assert.assertEquals("Text", activity.getText());
            String[] want = {"Tags"};
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertEquals(new Integer(27), activity.getDay());
            Assert.assertEquals(new Integer(1), activity.getMonth());
            Assert.assertEquals(new Integer(2004), activity.getYear());
            Assert.assertEquals(new Integer(16), activity.getCapacity());
        }
    }

    @Test
    public void updateTest() throws Exception {
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                put("/activity/" + id)
                        .param("user", userID.toString())
                        .param("token", token)
                .content("{\"text\":\"test\",\"tags\":[\"tag\"],\"title\":\"Test\",\"day\":28,\"month\":2,\"year\":2005,\"capacity\":17}")
                .contentType("application/json"))
                .andExpect(status().isOk());

        Assert.assertEquals(1, activityRepository.count());
        for (Activity activity: activityRepository.findAll()) {
            Assert.assertEquals("Test", activity.getTitle());
            Assert.assertEquals("test", activity.getText());
            String[] want = {"tag"};
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertEquals(new Integer(28), activity.getDay());
            Assert.assertEquals(new Integer(2), activity.getMonth());
            Assert.assertEquals(new Integer(2005), activity.getYear());
            Assert.assertEquals(new Integer(17), activity.getCapacity());
        }
    }

    @Test
    public void updateTestActivityNotFound() throws Exception {
        mockMvc.perform(
                put("/activity/9999")
                        .param("user", userID.toString())
                        .param("token", token)
                .content("{\"text\":\"Text\",\"tags\":[\"Tag1\",\"Tag2\"],\"title\":\"Title\",\"day\":27,\"month\":1,\"year\":2004,\"capacity\":16}")
                .contentType("application/json"))
                .andExpect(status().isBadRequest());

        Assert.assertEquals(0, activityRepository.count());
    }

    @Test
    public void updateTestInvalidUserId() throws Exception {
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                put("/activity/" + id)
                        .param("user", "9999")
                        .param("token", token)
                        .content("{\"text\":\"test\",\"tags\":[\"tag\"],\"title\":\"Test\",\"day\":28,\"month\":2,\"year\":2005,\"capacity\":17}")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, activityRepository.count());
        for (Activity activity: activityRepository.findAll()) {
            Assert.assertEquals("Title", activity.getTitle());
            Assert.assertEquals("Text", activity.getText());
            String[] want = {"Tags"};
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertEquals(new Integer(27), activity.getDay());
            Assert.assertEquals(new Integer(1), activity.getMonth());
            Assert.assertEquals(new Integer(2004), activity.getYear());
            Assert.assertEquals(new Integer(16), activity.getCapacity());
        }
    }

    @Test
    public void updateTestInvalidToken() throws Exception {
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                put("/activity/" + id)
                        .param("user", userID.toString())
                        .param("token", "INVALID_TOKEN")
                        .content("{\"text\":\"test\",\"tags\":[\"tag\"],\"title\":\"Test\",\"day\":28,\"month\":2,\"year\":2005,\"capacity\":17}")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, activityRepository.count());
        for (Activity activity: activityRepository.findAll()) {
            Assert.assertEquals("Title", activity.getTitle());
            Assert.assertEquals("Text", activity.getText());
            String[] want = {"Tags"};
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertEquals(new Integer(27), activity.getDay());
            Assert.assertEquals(new Integer(1), activity.getMonth());
            Assert.assertEquals(new Integer(2004), activity.getYear());
            Assert.assertEquals(new Integer(16), activity.getCapacity());
        }
    }

    @Test
    public void updateTestUnauthorizedUser() throws Exception {
        // create unauthorized user
        final IUAUser user = userRepository.save(new IUAUser("Name", "mail@mail.mail", "test", ""));
        tokenRepository.save(new Token(user.getId(), "TEST_TOKEN"));
        // create activity with authorized user
        final Long id = activityRepository.save(new Activity(27, 1, 2004, 16, userID, "Title", "Text", "Tags")).getId();

        mockMvc.perform(
                put("/activity/" + id)
                        .param("user", user.getId().toString())
                        .param("token", "TEST_TOKEN")
                        .content("{\"text\":\"test\",\"tags\":[\"tag\"],\"title\":\"Test\",\"day\":28,\"month\":2,\"year\":2005,\"capacity\":17}")
                        .contentType("application/json"))
                .andExpect(status().isUnauthorized());

        Assert.assertEquals(1, activityRepository.count());
        for (Activity activity: activityRepository.findAll()) {
            Assert.assertEquals("Title", activity.getTitle());
            Assert.assertEquals("Text", activity.getText());
            String[] want = {"Tags"};
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertArrayEquals(want, activity.getTags());
            Assert.assertEquals(new Integer(27), activity.getDay());
            Assert.assertEquals(new Integer(1), activity.getMonth());
            Assert.assertEquals(new Integer(2004), activity.getYear());
            Assert.assertEquals(new Integer(16), activity.getCapacity());
        }

        // delete unauthorized user
        userRepository.delete(user.getId());
        tokenRepository.delete(user.getId());
    }

}
