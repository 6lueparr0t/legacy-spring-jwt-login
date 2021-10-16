package com.spring_jwt_server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring_jwt_server.Application;
import com.spring_jwt_server.domain.Login;
import com.spring_jwt_server.domain.token.Token;
import com.spring_jwt_server.domain.token.TokenRepository;
import static com.spring_jwt_server.domain.token.UseCode.NOT;
import com.spring_jwt_server.domain.user.User;
import com.spring_jwt_server.domain.user.UserRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes= Application.class, webEnvironment = RANDOM_PORT)
public class SignControllerTest {
    @LocalServerPort
    private int port;

    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void tearDown() throws Exception {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
    }

    @Test
    public void sign_up_test() throws Exception {
        //given
        String userName = "testname";
        String userId = "testid";
        String userPassword = "testpw";

        User user = User.builder()
                .userName(userName)
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String url = "http://localhost:" + port + "/sign/up";

        //when
        mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated());

        //then
        List<User> all = userRepository.findAll();
        assertThat(all.get(0).getUserName()).isEqualTo(userName);
        assertThat(all.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    public void sign_in_test() throws Exception {
        //given
        String userName = "testname2";
        String userId = "testid2";
        String userPassword = "testpw2";

        User user = User.builder()
                .userName(userName)
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String url1 = "http://localhost:" + port + "/sign/up";

        //when
        mvc.perform(post(url1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated());

        Login login = Login.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String url2 = "http://localhost:" + port + "/sign/in";

        //when
        mvc.perform(post(url2)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", equalTo("success")))
                .andExpect(jsonPath("$.token.access").exists());

        //then
        List<Token> all = tokenRepository.findAll();
        assertThat(all.get(0).getUserId()).isEqualTo(userId);
    }

    @Test
    public void sign_check_test() throws Exception {
        //given
        String userName = "testname3";
        String userId = "testid3";
        String userPassword = "testpw3";

        User user = User.builder()
                .userName(userName)
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String url1 = "http://localhost:" + port + "/sign/up";

        //when
        mvc.perform(post(url1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated());

        Login login = Login.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String url2 = "http://localhost:" + port + "/sign/in";

        //when
        mvc.perform(post(url2)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(login)))
                .andExpect(status().isOk());

        //then
        List<Token> all = tokenRepository.findAll();
        String accessToken = all.get(0).getAccess();

        String url3 = "http://localhost:" + port + "/sign/check";
        mvc.perform(get(url3)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(new ObjectMapper().writeValueAsString("")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", equalTo("success")));
    }

    @Test
    public void sign_out_test() throws Exception {
        //given
        String userName = "testname4";
        String userId = "testid4";
        String userPassword = "testpw4";

        User user = User.builder()
                .userName(userName)
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String url1 = "http://localhost:" + port + "/sign/up";

        //when
        mvc.perform(post(url1)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isCreated());

        Login login = Login.builder()
                .userId(userId)
                .userPassword(userPassword)
                .build();

        String url2 = "http://localhost:" + port + "/sign/in";

        //when
        mvc.perform(post(url2)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(new ObjectMapper().writeValueAsString(login)))
                .andExpect(status().isOk());

        //then
        List<Token> before = tokenRepository.findAll();
        String accessToken = before.get(0).getAccess();

        String url3 = "http://localhost:" + port + "/sign/check";
        mvc.perform(get(url3)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(new ObjectMapper().writeValueAsString("")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", equalTo("success")));

        String url4 = "http://localhost:" + port + "/sign/out";
        mvc.perform(get(url4)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .content(new ObjectMapper().writeValueAsString("")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", equalTo("success")));

        List<Token> after = tokenRepository.findAll();
        assertThat(after.get(0).getUseCode()).isEqualTo(NOT);
    }
}
