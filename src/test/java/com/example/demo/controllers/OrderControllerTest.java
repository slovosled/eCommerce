package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    @Before
    public void init() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void submitOrderHappyPath() {
        User user = new User();
        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);

        BigDecimal firstPrice = new BigDecimal(12);
        BigDecimal secondPrice = new BigDecimal(15);

        Item item1 = new Item();
        item1.setId(1L);
        item1.setPrice(firstPrice);
        Item item2 = new Item();
        item2.setId(2L);
        item2.setPrice(secondPrice);
        cart.addItem(item1);
        cart.addItem(item2);
        user.setCart(cart);
        cart.setTotal(BigDecimal.valueOf(firstPrice.doubleValue() + secondPrice.doubleValue()));
        when(userRepository.findByUsername(Matchers.anyString())).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("fero");
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(cart.getItems().size(), response.getBody().getItems().size());
        assertEquals(cart.getTotal(), response.getBody().getTotal());
    }

    @Test
    public void submitOrderNullUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<UserOrder> response = orderController.submit("fero");
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

    @Test
    public void getOrderForUserHappyPath() {
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        when(orderRepository.findByUser(any())).thenReturn(new ArrayList<>(Arrays.asList(new UserOrder(), new UserOrder())));

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("anyFakeUser");
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(2, response.getBody().size());
    }

    @Test
    public void getOrderForUserNullUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("anyFakeUser");
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

}
