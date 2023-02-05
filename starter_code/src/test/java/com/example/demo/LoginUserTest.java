package com.example.demo;

import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.example.demo.TestUtils.getTestUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

// while tests in controllersTest folder uses mocks for testing, tests in this class
// are for testing REST endpoints for user login workflow, and this class doesn't use
// any mocks.
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
// this is a hack, so that we run createUserRestTest() first and then run loginUserRestTest().
// loginUserRestTest() doesn't create test user itself, so it depends on createUserRestTest()
// being run first to create the test user for it to use.
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LoginUserTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createUserRestTest() {
        User testUser = getTestUser();
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername(testUser.getUsername());
        createUserRequest.setPassword(testUser.getPassword());
        createUserRequest.setConfirmPassword(testUser.getPassword());

        ResponseEntity<User> response = restTemplate.postForEntity("http://localhost:" + port + "/api/user/create", createUserRequest, User.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(testUser.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void loginUserRestTest() {
        // user is created in createUserRestTest, so we don't need to create another one here.
        User testUser = getTestUser();
        Map<String, String> credentials = new HashMap<>();
        credentials.put("username", testUser.getUsername());
        credentials.put("password", testUser.getPassword());
        ResponseEntity<Void> loginResponse = restTemplate.postForEntity("http://localhost:" + port + "/login", credentials, Void.class);
        // we will get an JWT authorization token after we log in. Assert that this token is not null.
        assertNotNull(loginResponse.getHeaders().get("Authorization"));
        assertEquals(200, loginResponse.getStatusCodeValue());
    }

}