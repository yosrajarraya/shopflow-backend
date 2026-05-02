package com.shopflow.service;

import com.shopflow.dto.request.CartItemRequest;
import com.shopflow.entity.*;
import com.shopflow.enums.Role;
import com.shopflow.exception.BusinessException;
import com.shopflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User client;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        client = User.builder()
                .id(1L)
                .email("client@test.com")
                .role(Role.CUSTOMER)
                .build();

        cart = Cart.builder()
                .id(1L)
                .customer(client)
                .lignes(new ArrayList<>())
                .build();

        product = Product.builder()
                .id(1L)
                .nom("Laptop Test")
                .prix(500.0)
                .stock(5)
                .actif(true)
                .images(new ArrayList<>())
                .build();
    }

    @Test
    void ajouterArticle_doitFonctionner_siStockSuffisant() {
        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenReturn(cart);

        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantite(2);

        var response = cartService.ajouterArticle("client@test.com", request);

        assertNotNull(response);
        assertEquals(1, cart.getLignes().size());
        assertEquals(2, cart.getLignes().get(0).getQuantite());
    }

    @Test
    void ajouterArticle_doitEchouer_siStockInsuffisant() {
        product.setStock(1); // Stock insuffisant

        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantite(5); // Demande 5, stock = 1

        assertThrows(BusinessException.class, () ->
                cartService.ajouterArticle("client@test.com", request));
    }

    @Test
    void ajouterArticle_doitEchouer_siProduitInactif() {
        product.setActif(false);

        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        CartItemRequest request = new CartItemRequest();
        request.setProductId(1L);
        request.setQuantite(1);

        assertThrows(BusinessException.class, () ->
                cartService.ajouterArticle("client@test.com", request));
    }
}
