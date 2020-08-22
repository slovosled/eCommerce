package com.example.demo.security;


import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserDetailServiceImplTest {

    private UserDetailServiceImpl userDetailService;

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void init() {
        userDetailService = new UserDetailServiceImpl();
        TestUtils.injectObjects(userDetailService, "userRepository", userRepository);
    }

    @Test
    public void loadByUserName() {
        User user = new User();
        user.setUsername("FakeUser");
        user.setPassword("FakePassword");
        when(userRepository.findByUsername("FakeUser")).thenReturn(user);

        UserDetails result = userDetailService.loadUserByUsername("FakeUser");
        assertEquals(user.getUsername(), result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
    }

    @Test
    public void loadByUserNameNullUser() {
        when(userRepository.findByUsername("FakeUser")).thenReturn(null);
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailService.loadUserByUsername("FakeUser");
        });

        assertEquals(exception.getMessage(), "FakeUser");

    }


}
