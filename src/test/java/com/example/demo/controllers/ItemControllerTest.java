package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void init() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsHappyPath() {
        ArrayList<Item> items = new ArrayList<>(Arrays.asList(new Item(), new Item()));
        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response.getBody());
        assertEquals(items.size(), response.getBody().size());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void getItemByIdHappyPath() {
        Item item = new Item();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        ResponseEntity<Item> response = itemController.getItemById(1L);
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
    }

    @Test
    public void getItemsByNameHappyPath() {
        ArrayList<Item> items = new ArrayList<>(Arrays.asList(new Item(), new Item()));
        when(itemRepository.findByName(anyString())).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("anyName");
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(items.size(), response.getBody().size());
    }

    @Test
    public void getItemsByNameNullItems() {
        when(itemRepository.findByName(anyString())).thenReturn(null);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("anyName");
        assertNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
    }

}
