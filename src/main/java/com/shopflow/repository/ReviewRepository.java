package com.shopflow.repository;

import com.shopflow.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT COALESCE(AVG(r.note), 0) FROM Review r WHERE r.approuve = true")
    Double calculerNoteMoyenneGlobale();

    // Avis approuvés d'un produit
    List<Review> findByProductIdAndApprouveTrue(Long productId);

    // Tous les avis d'un produit (approuvés + en attente), triés par date
    List<Review> findByProductIdOrderByDateCreationDesc(Long productId);

    // Vérifier si un client a déjà noté un produit
    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    // Vérifier si un client a déjà noté un produit pour une commande spécifique
    boolean existsByCustomerIdAndProductIdAndOrderId(Long customerId, Long productId, Long orderId);

    // Calculer la note moyenne d'un produit
    @Query("SELECT AVG(r.note) FROM Review r WHERE r.product.id = :productId AND r.approuve = true")
    Double calculerNoteMoyenne(@Param("productId") Long productId);

    // Tous les avis pour les produits d'un vendeur
    List<Review> findByProductSellerId(Long sellerId);

    List<Review> findAllByOrderByDateCreationDesc();

    List<Review> findByApprouveFalseOrderByDateCreationDesc();
}
