package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void init() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void findByIDHappyPath() {
        User user = new User();
        user.setId(1L);
        user.setUsername("FakeUser");
        user.setPassword("FakePassword");
        user.setCart(new Cart());
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.findById(1L);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(1, response.getBody().getId());
        assertEquals("FakePassword", response.getBody().getPassword());
    }

    @Test
    public void findByUserNameHappyPath() {
        User user = new User();
        user.setId(1L);
        user.setUsername("FakeUser");
        user.setPassword("FakePassword");
        user.setCart(new Cart());

        when(userRepository.findByUsername(anyString())).thenReturn(user);

        ResponseEntity<User> response = userController.findByUserName("FakeUser");
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(1, response.getBody().getId());
        assertEquals("FakeUser", response.getBody().getUsername());
        assertEquals("FakePassword", response.getBody().getPassword());

    }

    @Test
    public void findByUserNameNullUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<User> response = userController.findByUserName("fakeUser");
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        ;
    }

    @Test
    public void createUserHappyPath() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("FakeUser");
        request.setPassword("abcdefg");
        request.setConfirmPassword("abcdefg");

        when(bCryptPasswordEncoder.encode("abcdefg")).thenReturn("encryptedPassword");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(request.getUsername(), response.getBody().getUsername());
        assertEquals("encryptedPassword", response.getBody().getPassword());
    }

    @Test
    public void createUserPasswordNull() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("FakeUser");
        request.setPassword(null);
        request.setConfirmPassword("abcdefg");

        ResponseEntity<User> response = userController.createUser(request);
        User userResponse = response.getBody();
        assertNull(userResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());

    }

    @Test
    public void createUserConfirmPasswordNull() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("FakeUser");
        request.setPassword("FakeUser");
        request.setConfirmPassword(null);

        ResponseEntity<User> response = userController.createUser(request);
        User userResponse = response.getBody();
        assertNull(userResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());

    }

    @Test
    public void createUserShortPassword() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("fa");
        request.setPassword("fa");
        request.setConfirmPassword("abcdefg");

        ResponseEntity<User> response = userController.createUser(request);
        User userResponse = response.getBody();
        assertNull(userResponse);
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode().value());

    }

}
