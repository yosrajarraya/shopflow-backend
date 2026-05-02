package com.shopflow.repository;

import com.shopflow.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    // Trouver les produits actifs paginés
    Page<Product> findByActifTrue(Pageable pageable);

    // Produits d'un vendeur
    Page<Product> findBySellerId(Long sellerId, Pageable pageable);

    // Recherche plein texte sur nom ou description
    @Query("SELECT p FROM Product p WHERE p.actif = true AND " +
           "(LOWER(p.nom) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Product> rechercherProduits(@Param("query") String query, Pageable pageable);

    // Produits en promotion
    @Query("SELECT p FROM Product p WHERE p.actif = true AND p.prixPromo IS NOT NULL")
    Page<Product> findPromos(Pageable pageable);

    // Top 10 meilleures ventes
    List<Product> findTop10ByActifTrueOrderByNombreVentesDesc();

    // Produits par catégorie
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.id = :categoryId AND p.actif = true")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);

    // Produits avec filtre de prix
    Page<Product> findByActifTrueAndPrixBetween(Double prixMin, Double prixMax, Pageable pageable);
}
