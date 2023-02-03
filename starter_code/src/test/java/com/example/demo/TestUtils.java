package com.example.demo;

import com.example.demo.model.persistence.User;

import java.lang.reflect.Field;

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

}
