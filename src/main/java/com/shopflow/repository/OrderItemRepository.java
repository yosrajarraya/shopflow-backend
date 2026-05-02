package com.shopflow.repository;

import com.shopflow.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Vérifier si un client a acheté un produit (pour autoriser l'avis)
    @Query("SELECT COUNT(oi) > 0 FROM OrderItem oi WHERE oi.product.id = :productId " +
           "AND oi.order.customer.id = :customerId AND oi.order.statut = 'DELIVERED'")
    boolean clientAAcheteProduit(@Param("customerId") Long customerId,
                                  @Param("productId") Long productId);
}
