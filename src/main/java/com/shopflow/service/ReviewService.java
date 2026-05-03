package com.shopflow.service;

import com.shopflow.dto.request.ReviewRequest;
import com.shopflow.dto.response.ReviewResponse;
import com.shopflow.entity.Order;
import com.shopflow.entity.Product;
import com.shopflow.entity.Review;
import com.shopflow.entity.User;
import com.shopflow.enums.OrderStatus;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.OrderRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.ReviewRepository;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    // Poster un avis (seulement si la commande est acceptée/livrée)
    @Transactional
    public ReviewResponse posterAvis(String email, ReviewRequest request) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Commande", request.getOrderId()));

        // Vérifier que la commande appartient au client
        if (!order.getCustomer().getId().equals(customer.getId())) {
            throw new BusinessException("Cette commande ne vous appartient pas");
        }

        // Vérifier que la commande est acceptée (PROCESSING, SHIPPED ou DELIVERED)
        if (order.getStatut() != OrderStatus.PROCESSING && 
            order.getStatut() != OrderStatus.SHIPPED && 
            order.getStatut() != OrderStatus.DELIVERED) {
            throw new BusinessException("Vous pouvez seulement noter une commande acceptée par le vendeur");
        }

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.getProductId()));

        // Vérifier que le produit est dans la commande
        boolean productInOrder = order.getLignes().stream()
                .anyMatch(item -> item.getProduct().getId().equals(product.getId()));
        if (!productInOrder) {
            throw new BusinessException("Ce produit n'est pas dans cette commande");
        }

        // Vérifier qu'il n'a pas déjà noté ce produit depuis cette commande
        if (reviewRepository.existsByCustomerIdAndProductIdAndOrderId(customer.getId(), product.getId(), request.getOrderId())) {
            throw new BusinessException("Vous avez déjà noté ce produit pour cette commande");
        }

        Review review = Review.builder()
                .customer(customer)
                .product(product)
                .order(order)
                .note(request.getNote())
                .commentaire(request.getCommentaire())
                .approuve(true)  // Visible immédiatement, l'admin peut désapprouver ensuite
                .build();

        reviewRepository.save(review);
        return convertirEnResponse(review);
    }

    // Voir les avis d'un produit (tous — approuvés et en attente)
    public List<ReviewResponse> voirAvisProduit(Long productId) {
        return reviewRepository.findByProductIdOrderByDateCreationDesc(productId)
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // Désapprouver un avis (admin — masquer sans supprimer)
    @Transactional
    public ReviewResponse desapprouverAvis(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", id));
        review.setApprouve(false);
        reviewRepository.save(review);
        return convertirEnResponse(review);
    }

    // Approuver un avis (admin)
    @Transactional
    public ReviewResponse approuverAvis(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", id));
        review.setApprouve(true);
        reviewRepository.save(review);
        return convertirEnResponse(review);
    }

    // Voir tous les avis des produits d'un vendeur
    public List<ReviewResponse> voirAvisVendeur(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        List<Review> reviews = reviewRepository.findByProductSellerId(seller.getId());
        return reviews.stream().map(this::convertirEnResponse).collect(Collectors.toList());
    }

    // Admin: lister tous les avis
    public List<ReviewResponse> listerTousAvis() {
        return reviewRepository.findAllByOrderByDateCreationDesc()
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // Admin: lister les avis en attente de modération
    public List<ReviewResponse> listerAvisEnAttente() {
        return reviewRepository.findByApprouveFalseOrderByDateCreationDesc()
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // Admin: rejeter/supprimer un avis
    @Transactional
    public void rejeterAvis(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avis", id));
        reviewRepository.delete(review);
    }

    private ReviewResponse convertirEnResponse(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .customerId(r.getCustomer().getId())
                .customerNom(r.getCustomer().getPrenom() + " " + r.getCustomer().getNom())
                .note(r.getNote())
                .commentaire(r.getCommentaire())
                .dateCreation(r.getDateCreation())
                .approuve(r.isApprouve())
                .build();
    }
}
