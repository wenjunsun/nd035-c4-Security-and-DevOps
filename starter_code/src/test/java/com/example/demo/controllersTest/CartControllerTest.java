package com.example.demo.controllersTest;

import com.example.demo.TestUtils;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static com.example.demo.TestUtils.getTestItems;
import static com.example.demo.TestUtils.getTestUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void addToCartTest() {
        User testUser = getTestUser();
        Item testItem = getTestItems().get(0);
        Cart testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testUser.setCart(testCart);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        ModifyCartRequest request = new ModifyCartRequest();

        // test using add to cart for a non-existing user
        request.setUsername("unknown username");
        ResponseEntity<Cart> response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        request.setUsername(testUser.getUsername()); // fixes username of request to be an existing user.

        // test using add to cart for a non-existing item
        request.setItemId(-1);
        response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
        request.setItemId(testItem.getId()); // fixes item id of request to be an existing item.

        // finally test happy path for addToCart()
        int quantity = 5;
        request.setQuantity(quantity);
        response = cartController.addTocart(request);
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        Cart cartCreated = response.getBody();
        assertNotNull(cartCreated);
        assertEquals(testCart.getId(), cartCreated.getId());
        assertEquals(quantity, cartCreated.getItems().size());
        assertEquals(testItem, cartCreated.getItems().get(0));
        assertEquals(testItem.getPrice().multiply(new BigDecimal(quantity)), cartCreated.getTotal());
    }

    @Test
    public void removeFromCartTest() {
        User testUser = getTestUser();
        Item testItem = getTestItems().get(0);
        Cart testCart = new Cart();
        testCart.setId(1L);
        testCart.setUser(testUser);
        testUser.setCart(testCart);
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
        when(itemRepository.findById(testItem.getId())).thenReturn(Optional.of(testItem));

        ModifyCartRequest request = new ModifyCartRequest();
        // first need to add to cart, then we can test remove from cart.
        int quantity = 5;
        request.setUsername(testUser.getUsername());
        request.setItemId(testItem.getId());
        request.setQuantity(quantity);
        cartController.addTocart(request);

        // test using remove from cart for a non-existing user
        request.setUsername("unknown username");
        ResponseEntity<Cart> response = cartController.removeFromcart(request);
        assertEquals(404, response.getStatusCodeValue());
        request.setUsername(testUser.getUsername());

        // test using remove from cart for a non-existing item
        request.setItemId(-1);
        response = cartController.removeFromcart(request);
        assertEquals(404, response.getStatusCodeValue());
        request.setItemId(testItem.getId());

        // finally test happy path for removeFromCart()
        int quantityToRemove = 2;
        request.setQuantity(quantityToRemove);
        response = cartController.removeFromcart(request);
        assertNotNull(response);
        Cart cartAfterRemoval = response.getBody();
        assertNotNull(cartAfterRemoval);
        assertEquals(testCart.getId(), cartAfterRemoval.getId());
        // we added 5 items but removed 2, so there should be 5 - 2 = 3 left.
        assertEquals(quantity - quantityToRemove, cartAfterRemoval.getItems().size());
        assertEquals(testItem, cartAfterRemoval.getItems().get(0));
        assertEquals(testUser, cartAfterRemoval.getUser());
        assertEquals(testItem.getPrice().multiply(new BigDecimal(quantity - quantityToRemove)), cartAfterRemoval.getTotal());
    }

}
