package com.mhkj;

import com.mhkj.sender.SenderApplication;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest(classes = SenderApplication.class)
public class MqTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testSendDirectMessage() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/sendDirectMessage")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("test send direct message")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSendFanoutMessage() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/sendFanoutMessage")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("test send fanout message")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSendTopicMessage() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .post("/sendTopicMessage")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("test send topic message")
        )
        .andDo(MockMvcResultHandlers.print())
        .andReturn();
        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
