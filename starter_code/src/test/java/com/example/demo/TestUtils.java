package com.example.demo;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

// class that contains useful methods/objects for unit testing purposes.
public class TestUtils {
    // This method allows us to inject mocks into the public/private fields of objects.
    public static void injectObject(Object target, String fieldName, Object toInject) {
        boolean wasPrivate = false;
        try {
            Field f = target.getClass().getDeclaredField(fieldName);
            if (! f.isAccessible()) {
                f.setAccessible(true);
                wasPrivate = true;
            }
            f.set(target, toInject);
            if (wasPrivate) {
                f.setAccessible(false);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    // return an example user object for use in our unit tests.
    public static User getTestUser() {
        User testUser = new User();
        testUser.setId(1);
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");
        return testUser;
    }

    public static List<Item> getTestItems() {
        Item testItem1 = new Item();
        testItem1.setId(1L);
        testItem1.setDescription("Brand new Macbook!");
        testItem1.setName("Macbook");
        testItem1.setPrice(BigDecimal.valueOf(1000));

        Item testItem2 = new Item();
        testItem2.setId(2L);
        testItem2.setDescription("Brand new iPad!");
        testItem2.setName("iPad");
        testItem2.setPrice(BigDecimal.valueOf(500));

        List<Item> itemsList = new ArrayList<>();
        itemsList.add(testItem1);
        itemsList.add(testItem2);
        return itemsList;
    }

}
