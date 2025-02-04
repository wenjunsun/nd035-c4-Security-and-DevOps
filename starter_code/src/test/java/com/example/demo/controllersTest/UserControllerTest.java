package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static com.example.demo.TestUtils.getTestUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;
    private UserRepository userRepo = mock(UserRepository.class);
    private CartRepository cartRepo = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepo);
        TestUtils.injectObject(userController, "cartRepository", cartRepo);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUserTest() {
        when(encoder.encode("testPassword")).thenReturn("hashedPassword");
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setConfirmPassword("testPassword");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        User u = response.getBody();
        assertNotNull(u);
        assertEquals(0, u.getId());
        assertEquals("testUser", u.getUsername());
        assertEquals("hashedPassword", u.getPassword());
    }

    @Test
    public void createUserErrorTest() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("testUser");
        request.setPassword("testPassword");
        request.setConfirmPassword("test");
        ResponseEntity<User> response = userController.createUser(request);
        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    public void findByUserNameTest() {
        User testUser = getTestUser();
        when(userRepo.findByUsername("testUser")).thenReturn(testUser);
        ResponseEntity<User> response = userController.findByUserName("testUser");
        assertEquals(200, response.getStatusCodeValue());
        User userFound = response.getBody();
        assertNotNull(userFound);
        assertEquals(1, userFound.getId());
        assertEquals("testUser", userFound.getUsername());
        assertEquals("testPassword", userFound.getPassword());
    }

    @Test
    public void findByIdTest() {
        User testUser = getTestUser();
        when(userRepo.findById(1L)).thenReturn(Optional.of(testUser));
        ResponseEntity<User> response = userController.findById(1L);
        assertEquals(200, response.getStatusCodeValue());
        User userFound = response.getBody();
        assertNotNull(userFound);
        assertEquals(1, userFound.getId());
        assertEquals("testUser", userFound.getUsername());
        assertEquals("testPassword", userFound.getPassword());
    }

}
