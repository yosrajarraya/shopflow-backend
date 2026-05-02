package com.shopflow.enums;

// Statuts possibles d'une commande
public enum OrderStatus {
    PENDING,     // En attente de paiement
    PAID,        // Payée
    PROCESSING,  // En cours de préparation
    SHIPPED,     // Expédiée
    DELIVERED,   // Livrée
    CANCELLED    // Annulée
}
