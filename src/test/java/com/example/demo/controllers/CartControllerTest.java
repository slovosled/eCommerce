package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void AddItemToCartHappyPath() {

        final String expectedUserName = "testUser";
        final BigDecimal expectedPrice = new BigDecimal("12.0");
        final int quantity = 3;

        //Create Stubbed Item
        User stubbedUser = new User();
        stubbedUser.setUsername(expectedUserName);
        stubbedUser.setCart(new Cart());

        //Create Stubbed Item
        Item item = new Item();
        item.setPrice(expectedPrice);
        Optional<Item> stubbedOptionalItem = Optional.of(item);

        when(itemRepository.findById(anyLong())).thenReturn(stubbedOptionalItem);
        when(userRepository.findByUsername(anyString())).thenReturn(stubbedUser);

        // Create Cart request
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(1);
        cartRequest.setQuantity(quantity);
        cartRequest.setUsername("testUser");

        final ResponseEntity<Cart> response = cartController.addTocart(cartRequest);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(quantity, cart.getItems().toArray().length);
        assertEquals(quantity * expectedPrice.intValue(), cart.getTotal().intValue());
    }

    @Test
    public void AddItemToCartNullUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<Cart> cartResponse = cartController.addTocart(new ModifyCartRequest());
        assertNull(cartResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), cartResponse.getStatusCodeValue());
    }

    @Test
    public void AddItemToCartNullItem() {
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Cart> cartResponse = cartController.addTocart(new ModifyCartRequest());
        assertNull(cartResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), cartResponse.getStatusCodeValue());
    }

    @Test
    public void RemoveItemFromCartHappyPath() {
        final BigDecimal expectedPrice = new BigDecimal(12);
        final int quantity_to_remove = 2;
        final int quantity = 3;

        //Create Stubbed Item
        User stubbedUser = new User();
        Cart stubbedCart = new Cart();

        // Create Fake Items
        Item item1 = new Item();
        item1.setPrice(expectedPrice);
        item1.setId(1L);

        Item item2 = new Item();
        item2.setPrice(expectedPrice);
        item2.setId(1L);

        Item item3 = new Item();
        item3.setPrice(expectedPrice);
        item3.setId(1L);


        stubbedCart.addItem(item1);
        stubbedCart.addItem(item2);
        stubbedCart.addItem(item3);
        stubbedUser.setCart(stubbedCart);

        //Create Stubbed Item
        Optional<Item> stubbedOptionalItem = Optional.of(item1);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setQuantity(quantity_to_remove);

        when(itemRepository.findById(anyLong())).thenReturn(stubbedOptionalItem);
        when(userRepository.findByUsername(anyString())).thenReturn(stubbedUser);

        final ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        final Cart cart = response.getBody();
        assertNotNull(cart);
        assertEquals(quantity - quantity_to_remove, cart.getItems().toArray().length);
        assertEquals((quantity - quantity_to_remove) * expectedPrice.intValue(), cart.getTotal().intValue());
    }

    @Test
    public void RemoveItemFromCartNullUser() {
        when(userRepository.findByUsername(anyString())).thenReturn(null);
        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(new ModifyCartRequest());
        assertNull(cartResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), cartResponse.getStatusCodeValue());
    }

    @Test
    public void RemoveItemFromCartNullItem() {
        when(userRepository.findByUsername(anyString())).thenReturn(new User());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Cart> cartResponse = cartController.removeFromcart(new ModifyCartRequest());
        assertNull(cartResponse.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), cartResponse.getStatusCodeValue());
    }


}
