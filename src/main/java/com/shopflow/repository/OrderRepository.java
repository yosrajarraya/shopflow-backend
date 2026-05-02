package com.shopflow.repository;

import com.shopflow.entity.Order;
import com.shopflow.enums.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Commandes d'un client
    List<Order> findByCustomerIdOrderByDateCommandeDesc(Long customerId);

    // Compter les commandes d'un client
    Long countByCustomerId(Long customerId);

    // Trouver par numéro de commande
    Optional<Order> findByNumeroCommande(String numeroCommande);

    // Dernier suffixe utilisé pour un préfixe de numéro de commande
    @Query(value = """
            SELECT COALESCE(MAX(CAST(SUBSTRING(numero_commande, 10) AS UNSIGNED)), 0)
            FROM orders
            WHERE numero_commande LIKE CONCAT(:prefix, '%')
            """, nativeQuery = true)
    Long findMaxNumeroCommandeSuffixByPrefix(@Param("prefix") String prefix);

    // Commandes par statut
    List<Order> findByStatut(OrderStatus statut);

    // Stats: chiffre d'affaires total
    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o WHERE o.statut != 'CANCELLED'")
    Double calculerChiffreAffaires();

    // Commandes récentes pour admin
    Page<Order> findAllByOrderByDateCommandeDesc(Pageable pageable);

    // Compter les commandes en attente pour un vendeur
    @Query("SELECT COUNT(o) FROM Order o JOIN o.lignes ol WHERE ol.product.seller.id = :sellerId AND o.statut = 'PENDING'")
    Long compterCommandesEnAttente(@Param("sellerId") Long sellerId);

    // Commandes récentes pour un vendeur (distinctes si plusieurs lignes appartiennent au même ordre)
    @Query("SELECT DISTINCT o FROM Order o JOIN o.lignes ol WHERE ol.product.seller.id = :sellerId ORDER BY o.dateCommande DESC")
    org.springframework.data.domain.Page<Order> findBySellerIdOrderByDateCommandeDesc(@Param("sellerId") Long sellerId, org.springframework.data.domain.Pageable pageable);

    // Revenu du vendeur sur une période (ex: dernier mois)
    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o JOIN o.lignes ol WHERE ol.product.seller.id = :sellerId AND o.dateCommande >= :since")
    Double calculerRevenuDepuisPourVendeur(@Param("sellerId") Long sellerId, @Param("since") java.time.LocalDateTime since);
}
