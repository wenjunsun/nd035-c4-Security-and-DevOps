package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.TestUtils.getTestItems;
import static com.example.demo.TestUtils.getTestUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submitTest() {
        // Create a cart for a single item for a user for testing purposes.
        User testUser = getTestUser();
        Item testItem = getTestItems().get(0);
        Cart testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testUser.setCart(testCart);
        testCart.addItem(testItem);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);

        // test submit order for a non-existing user
        ResponseEntity<UserOrder> response = orderController.submit("unknown username");
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());

        // test happy path for submit order.
        response = orderController.submit(testUser.getUsername());
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        UserOrder userOrderSubmitted = response.getBody();
        assertNotNull(userOrderSubmitted);
        assertEquals(testUser, userOrderSubmitted.getUser());
        assertEquals(1, userOrderSubmitted.getItems().size());
        assertEquals(testItem, userOrderSubmitted.getItems().get(0));
        assertEquals(testItem.getPrice(), userOrderSubmitted.getTotal());
    }

    @Test
    public void getOrdersForUserTest() {
        // Create a cart for a single item for a user for testing purposes.
        User testUser = getTestUser();
        Item testItem = getTestItems().get(0);
        Cart testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testUser.setCart(testCart);
        testCart.addItem(testItem);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        
        // create an order for this user, so we can get some orders later.
        ResponseEntity<UserOrder> submitResponse = orderController.submit(testUser.getUsername());
        assertNotNull(submitResponse);
        assertEquals(200, submitResponse.getStatusCodeValue());
        UserOrder userOrder = submitResponse.getBody();
        List<UserOrder> userOrdersCreated = new ArrayList<>();
        userOrdersCreated.add(userOrder);
        when(orderRepository.findByUser(testUser)).thenReturn(userOrdersCreated);
        
        // test get orders for a non-existing user
        ResponseEntity<List<UserOrder>> getOrdersResponse = orderController.getOrdersForUser("unknown username");
        assertNotNull(getOrdersResponse);
        assertEquals(404, getOrdersResponse.getStatusCodeValue());
        
        // test happy path for getOrdersForUser()
        getOrdersResponse = orderController.getOrdersForUser(testUser.getUsername());
        assertNotNull(getOrdersResponse);
        List<UserOrder> userOrdersFound = getOrdersResponse.getBody();
        assertEquals(userOrdersCreated, userOrdersFound);
    }

}
