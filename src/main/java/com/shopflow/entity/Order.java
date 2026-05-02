package com.shopflow.entity;

import com.shopflow.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private User customer;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus statut = OrderStatus.PENDING;

    // Numéro unique de commande (ex: ORD-2024-00001)
    @Column(unique = true)
    private String numeroCommande;

    // Adresse de livraison (stockée en texte pour garder l'historique)
    private String adresseLivraison;

    // Notes optionnelles pour la commande
    private String notes;

    private Double sousTotal;
    private Double fraisLivraison;
    private Double totalTTC;

    @Builder.Default
    private LocalDateTime dateCommande = LocalDateTime.now();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> lignes = new ArrayList<>();

    // Pour notifier le client d'un changement de statut
    @Builder.Default
    private boolean isNew = true;
}
