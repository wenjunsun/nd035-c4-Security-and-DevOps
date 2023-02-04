package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.demo.TestUtils.getTestItems;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void getItemsTest() {
        List<Item> testItems = getTestItems();
        when(itemRepository.findAll()).thenReturn(testItems);
        ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        List<Item> itemsFound = response.getBody();
        // can use assertEquals() to compare two lists because
        // assertEquals() will use List.equals() to compare, which is
        // implemented by List class to compare element by element.
        assertEquals(testItems, itemsFound);
    }

    @Test
    public void getItemByIdTest() {
        List<Item> testItems = getTestItems();
        for (Item testItem: testItems) {
            when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));
        }
        ResponseEntity<Item> response;
        Item itemFound;
        for (Item testItem: testItems) {
            response = itemController.getItemById(testItem.getId());
            assertNotNull(response);
            itemFound = response.getBody();
            assertNotNull(itemFound);
            assertEquals(testItem, itemFound);
        }
    }

    @Test
    public void getItemsByNameTest() {
        List<Item> testItems = getTestItems();
        Item testItem = testItems.get(0);
        List<Item> listOfTestItem = new ArrayList<>();
        listOfTestItem.add(testItem);
        when(itemRepository.findByName(testItem.getName())).thenReturn(listOfTestItem);

        ResponseEntity<List<Item>> response = itemController.getItemsByName(testItem.getName());
        assertNotNull(response);
        List<Item> listOfItemFound = response.getBody();
        assertEquals(listOfTestItem, listOfItemFound);

        // test when we get items by name, but when that name doesn't have any
        // items corresponding to it.
        response = itemController.getItemsByName("unknown item name");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue()); // 404 is the status code for NOT FOUND
    }

}
