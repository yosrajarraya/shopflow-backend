package com.shopflow.service;

import com.shopflow.entity.*;
import com.shopflow.enums.OrderStatus;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Tests unitaires sur la couche Service avec Mockito
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartService cartService;

    @InjectMocks
    private OrderService orderService;

    private User client;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        // Préparer les données de test
        client = User.builder()
                .id(1L)
                .email("client@test.com")
                .role(Role.CUSTOMER)
                .prenom("Test")
                .nom("Client")
                .build();

        product = Product.builder()
                .id(1L)
                .nom("Produit Test")
                .prix(100.0)
                .stock(10)
                .nombreVentes(0)
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .product(product)
                .quantite(2)
                .build();

        cart = Cart.builder()
                .id(1L)
                .customer(client)
                .lignes(new ArrayList<>(List.of(cartItem)))
                .build();
        // Lier le cartItem au cart
        cart.getLignes().get(0).setCart(cart);
    }

    @Test
    void annulerCommande_doitFonctionner_siStatutPending() {
        // Préparer une commande PENDING
        OrderItem orderItem = OrderItem.builder()
                .product(product)
                .quantite(2)
                .prixUnitaire(100.0)
                .build();

        Order order = Order.builder()
                .id(1L)
                .customer(client)
                .statut(OrderStatus.PENDING)
                .numeroCommande("ORD-2025-00001")
                .lignes(new ArrayList<>(List.of(orderItem)))
                .build();
        orderItem.setOrder(order);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any())).thenReturn(product);
        when(orderRepository.save(any())).thenReturn(order);

        // Exécuter
        orderService.annulerCommande(1L, "client@test.com");

        // Vérifier
        assertEquals(OrderStatus.CANCELLED, order.getStatut());
        // Le stock doit être remis
        assertEquals(12, product.getStock());
        verify(orderRepository).save(order);
    }

    @Test
    void annulerCommande_doitEchouer_siStatutDelivered() {
        Order order = Order.builder()
                .id(1L)
                .customer(client)
                .statut(OrderStatus.DELIVERED)
                .numeroCommande("ORD-2025-00002")
                .lignes(new ArrayList<>())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // Doit lancer une exception
        assertThrows(BusinessException.class, () ->
                orderService.annulerCommande(1L, "client@test.com"));
    }

    @Test
    void passerCommande_doitEchouer_siPanierVide() {
        Cart panierVide = Cart.builder()
                .id(2L)
                .customer(client)
                .lignes(new ArrayList<>())
                .build();

        when(userRepository.findByEmail("client@test.com")).thenReturn(Optional.of(client));
        when(cartRepository.findByCustomerId(1L)).thenReturn(Optional.of(panierVide));

        var request = new com.shopflow.dto.request.OrderRequest();
        request.setAdresseLivraison("12 Rue Test, 1000 Tunis, Tunisie");

        assertThrows(BusinessException.class, () ->
                orderService.passerCommande("client@test.com", request));
    }

    @Test
    void mettreAJourStatut_doitChangerLeStatut() {
        User admin = User.builder()
                .id(99L)
                .email("admin@test.com")
                .role(Role.ADMIN)
                .prenom("Admin")
                .nom("Root")
                .build();

        Order order = Order.builder()
                .id(1L)
                .customer(client)
                .statut(OrderStatus.PAID)
                .numeroCommande("ORD-2025-00003")
                .lignes(new ArrayList<>())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(orderRepository.save(any())).thenReturn(order);

        orderService.mettreAJourStatut(1L, OrderStatus.PROCESSING, "admin@test.com");

        assertEquals(OrderStatus.PROCESSING, order.getStatut());
        assertTrue(order.isNew()); // Notification client
    }
}
