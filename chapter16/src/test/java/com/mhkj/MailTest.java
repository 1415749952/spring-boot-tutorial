package com.mhkj;

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
@SpringBootTest(classes = Application.class)
public class MailTest {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testSendText() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                .post("/sendText")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content("{\"email\":\"gongm_24@126.com\",\"subject\":\"测试发邮件\",\"content\":\"随便的内容\"}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
        .andDo(MockMvcResultHandlers.print())
        .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSendHtml() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .post("/sendHtml")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"email\":\"gongm_24@126.com\",\"subject\":\"测试发邮件\",\"content\":\"随便的内容\"}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSendFreemarker() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .post("/sendFreemarkerTpl")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"email\":\"gongm_24@126.com\"," +
                                "\"subject\":\"测试发送Freemarker模块邮件\"," +
                                "\"template\":\"welcome.ftl\"," +
                                "\"arguments\":{\"username\":\"哈哈哈哈\"}}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSendFreemarkerText() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .post("/sendFreemarkerText")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"email\":\"gongm_24@126.com\"," +
                                "\"subject\":\"测试发送Freemarker模块邮件\"," +
                                "\"content\":\"<h1>你好吗？${username}</h1>\"," +
                                "\"template\":\"welcome\"," +
                                "\"arguments\":{\"username\":\"哈哈哈哈\"}}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

    @Test
    public void testSendThymeleaf() throws Exception {
        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .post("/sendThymeleaf")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content("{\"email\":\"gongm_24@126.com\"," +
                                "\"subject\":\"测试发送Freemarker模块邮件\"," +
                                "\"template\":\"thymeleaf\"," +
                                "\"arguments\":{\"username\":\"哈哈哈哈\"}}")
        )
//        .andExpect(status().isOk());
//        .andExpect(content().string("hello"))
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        Assert.assertEquals(200, mvcResult.getResponse().getStatus());
    }

}
