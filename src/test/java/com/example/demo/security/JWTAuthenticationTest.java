package com.example.demo.security;


import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class JWTAuthenticationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void creationOfUser() {
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("Palo");
        userRequest.setPassword("abeceda");
        userRequest.setConfirmPassword("abeceda");
        ResponseEntity<User> response = this.restTemplate.postForEntity("http://localhost:" + port + "/api/user/create", userRequest, User.class);

        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void loginOfUser() {
        //Create user
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("AnotherUser");
        userRequest.setPassword("abeceda");
        userRequest.setConfirmPassword("abeceda");
        ResponseEntity<User> response = this.restTemplate.postForEntity("http://localhost:" + port + "/api/user/create", userRequest, User.class);

        User user = new User();
        user.setUsername("AnotherUser");
        user.setPassword("abeceda");

        ResponseEntity<String> loginResponse = this.restTemplate.postForEntity("http://localhost:" + port + "/login", user, String.class);
        assertEquals(HttpStatus.OK.value(), loginResponse.getStatusCodeValue());
        assertNotNull(loginResponse.getHeaders().get("Authorization"));
    }

    @Test
    public void ApiRestrictionWithoutLogin() {
        ResponseEntity<Item> response = this.restTemplate.getForEntity("http://localhost:" + port + "/api/item/1", Item.class);
        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCodeValue());
    }

}
