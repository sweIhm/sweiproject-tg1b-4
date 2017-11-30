package edu.hm.cs.iua;

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

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void listAllTest() throws Exception {
        mockMvc.perform(get("/activity"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(content().string("[]"));
    }

    @Test
    public void createTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity")
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(delete("/activity/" + id));
    }

    @Test
    public void createMultipleTest() throws Exception {
        MockHttpServletResponse response1 = mockMvc.perform(post("/activity")
                .content("{\"title\":\"Test1\",\"text\":\"test test1\",\"tags\":\"test1\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        MockHttpServletResponse response2 = mockMvc.perform(post("/activity")
                .content("{\"title\":\"Test2\",\"text\":\"test test2\",\"tags\":\"test2\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        char id1 = response1.getContentAsString().charAt(6);
        char id2 = response2.getContentAsString().charAt(6);
        mockMvc.perform(delete("/activity/" + id1));
        mockMvc.perform(delete("/activity/" + id2));
    }

    @Test
    public void findTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity")
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(get("/activity/" + id))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/activity/" + id));
    }

    @Test
    public void findTestFailed() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity")
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(get("/activity/0"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/activity/" + id));
    }

    @Test
    public void deleteTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity")
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(delete("/activity/" + id))
                .andExpect(status().isOk());
    }

    @Test
    public void updateTest() throws Exception {
        MockHttpServletResponse response = mockMvc.perform(post("/activity")
                .content("{\"title\":\"Test\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        char id = response.getContentAsString().charAt(6);
        mockMvc.perform(put("/activity/" + id)
                .content("{\"title\":\"TestTest\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/activity/" + id));
    }

    @Test
    public void updateTestFailed() throws Exception {
        mockMvc.perform(put("/activity/" + 0)
                .content("{\"title\":\"TestTest\",\"text\":\"test test\",\"tags\":\"test\"}")
                .contentType("application/json"))
                .andExpect(status().isOk());
    }

}
