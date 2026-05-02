package com.shopflow.service;

import com.shopflow.dto.request.ProductRequest;
import com.shopflow.dto.response.ProductResponse;
import com.shopflow.dto.response.ReviewResponse;
import com.shopflow.entity.Category;
import com.shopflow.entity.Product;
import com.shopflow.entity.User;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.enums.Role;
import com.shopflow.repository.CategoryRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.ReviewRepository;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    // Lister tous les produits actifs (paginé)
    public Page<ProductResponse> listerProduits(Pageable pageable) {
        return productRepository.findByActifTrue(pageable)
                .map(this::convertirEnResponse);
    }

    // Voir le détail d'un produit
    public ProductResponse voirProduit(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        return convertirEnResponseAvecDetails(product);
    }

    // Recherche plein texte
    public Page<ProductResponse> rechercher(String query, Pageable pageable) {
        return productRepository.rechercherProduits(query, pageable)
                .map(this::convertirEnResponse);
    }

    // Créer un produit (vendeur ou admin)
    @Transactional
    public ProductResponse creerProduit(ProductRequest request, String emailVendeur) {
        User vendeur = userRepository.findByEmail(emailVendeur)
                .orElseThrow(() -> new BusinessException("Vendeur non trouvé"));

        Set<Category> categories = new HashSet<>();
        if (request.getCategoryIds() != null) {
            categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
        }

        Product product = Product.builder()
                .seller(vendeur)
                .nom(request.getNom())
                .description(request.getDescription())
                .prix(request.getPrix())
                .prixPromo(request.getPrixPromo())
                .stock(request.getStock())
                .categories(categories)
                .images(request.getImages() != null ? request.getImages() : List.of())
                .build();

        productRepository.save(product);
        log.info("Produit créé: {} par {}", product.getNom(), emailVendeur);
        return convertirEnResponse(product);
    }

    // Modifier un produit
    @Transactional
    public ProductResponse modifierProduit(Long id, ProductRequest request, String emailUtilisateur) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));

        // Vérifier que c'est bien le vendeur du produit
        if (!product.getSeller().getEmail().equals(emailUtilisateur)) {
            User user = userRepository.findByEmail(emailUtilisateur).orElseThrow();
            if (user.getRole().name().equals("ADMIN") == false) {
                throw new BusinessException("Vous n'avez pas le droit de modifier ce produit");
            }
        }

        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setPrixPromo(request.getPrixPromo());
        product.setStock(request.getStock());

        if (request.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(request.getCategoryIds()));
            product.setCategories(categories);
        }

        if (request.getImages() != null) {
            product.setImages(request.getImages());
        }

        productRepository.save(product);
        return convertirEnResponse(product);
    }

    // Désactiver un produit (soft delete)
    @Transactional
    public void desactiverProduit(Long id, String emailUtilisateur) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit", id));
        
        User user = userRepository.findByEmail(emailUtilisateur)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur avec l'email " + emailUtilisateur + " n'existe pas"));
        
        // ADMIN peut supprimer n'importe quel produit
        // SELLER ne peut supprimer que ses propres produits
        boolean isAdmin = user.getRole().name().equals("ADMIN");
        boolean isOwner = product.getSeller().getId().equals(user.getId());
        
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Vous ne pouvez supprimer que vos propres produits");
        }
        
        product.setActif(false);
        productRepository.save(product);
        log.info("Produit désactivé: {} par {}", id, emailUtilisateur);
    }

    // Produits en promotion
    public Page<ProductResponse> voirPromos(Pageable pageable) {
        return productRepository.findPromos(pageable).map(this::convertirEnResponse);
    }

    // Top 10 meilleures ventes
    public List<ProductResponse> topVentes() {
        return productRepository.findTop10ByActifTrueOrderByNombreVentesDesc()
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // Statistiques publiques pour la page d'accueil
    public Map<String, Object> statistiquesAccueil() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProduits", productRepository.countByActifTrue());
        stats.put("totalVendeurs", userRepository.countByRoleAndActifTrue(Role.SELLER));
        stats.put("totalClients", userRepository.countByRoleAndActifTrue(Role.CUSTOMER));
        stats.put("noteMoyenne", reviewRepository.calculerNoteMoyenneGlobale());

        return stats;
    }

        // Produits du vendeur connecté (paginé)
        public Page<ProductResponse> listerMesProduits(String emailVendeur, Pageable pageable) {
        User vendeur = userRepository.findByEmail(emailVendeur)
            .orElseThrow(() -> new BusinessException("Vendeur non trouvé"));

            if (!"SELLER".equals(vendeur.getRole().name())) {
                throw new BusinessException("Accès réservé aux vendeurs");
            }

        return productRepository.findBySellerId(vendeur.getId(), pageable)
            .map(this::convertirEnResponse);
        }

    // =====================
    // Méthodes de conversion Entity -> DTO
    // =====================

    @Transactional(readOnly = true)
    public ProductResponse convertirEnResponse(Product p) {
        return ProductResponse.builder()
                .id(p.getId())
                .nom(p.getNom())
                .description(p.getDescription())
                .prix(p.getPrix())
                .prixPromo(p.getPrixPromo())
                .pourcentageRemise(p.getPourcentageRemise())
                .stock(p.getStock())
                .actif(p.isActif())
                .dateCreation(p.getDateCreation())
                .nombreVentes(p.getNombreVentes())
                .sellerId(p.getSeller().getId())
                .sellerNom(p.getSeller().getPrenom() + " " + p.getSeller().getNom())
                .sellerBoutique(p.getSeller().getSellerProfile() != null
                        ? p.getSeller().getSellerProfile().getNomBoutique() : null)
                .images(p.getImages())
                .categories(p.getCategories().stream()
                        .map(c -> new com.shopflow.dto.response.CategoryResponse(
                                c.getId(), c.getNom(), c.getDescription(), null, null))
                        .collect(Collectors.toSet()))
                .build();
    }

    // Version avec avis et variantes (pour la fiche détail)
    @Transactional(readOnly = true)
    private ProductResponse convertirEnResponseAvecDetails(Product p) {
        ProductResponse response = convertirEnResponse(p);

        // Ajouter les variantes
        List<ProductResponse.VariantResponse> variantes = p.getVariantes().stream()
                .map(v -> ProductResponse.VariantResponse.builder()
                        .id(v.getId())
                        .attribut(v.getAttribut())
                        .valeur(v.getValeur())
                        .stockSupplementaire(v.getStockSupplementaire())
                        .prixDelta(v.getPrixDelta())
                        .build())
                .collect(Collectors.toList());
        response.setVariantes(variantes);

        // Ajouter les avis approuvés
        List<ReviewResponse> avis = p.getAvis().stream()
                .filter(r -> r.isApprouve())
                .map(r -> ReviewResponse.builder()
                        .id(r.getId())
                        .customerId(r.getCustomer().getId())
                        .customerNom(r.getCustomer().getPrenom() + " " + r.getCustomer().getNom())
                        .note(r.getNote())
                        .commentaire(r.getCommentaire())
                        .dateCreation(r.getDateCreation())
                        .approuve(r.isApprouve())
                        .build())
                .collect(Collectors.toList());
        response.setAvis(avis);

        // Note moyenne
        Double noteMoyenne = reviewRepository.calculerNoteMoyenne(p.getId());
        response.setNoteMoyenne(noteMoyenne);

        return response;
    }
}
