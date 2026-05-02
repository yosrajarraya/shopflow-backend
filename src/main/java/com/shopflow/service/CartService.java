package com.shopflow.service;

import com.shopflow.dto.request.CartItemRequest;
import com.shopflow.dto.response.CartResponse;
import com.shopflow.entity.*;
import com.shopflow.enums.CouponType;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;

    // Prix de livraison fixe (simplifié)
    private static final double FRAIS_LIVRAISON = 7.0;
    private static final double LIVRAISON_GRATUITE_SEUIL = 100.0;

    // Voir le panier du client connecté
    public CartResponse voirPanier(String email) {
        Cart cart = trouverPanierParEmail(email);
        return convertirEnResponse(cart);
    }

    // Ajouter un article au panier
    @Transactional
    public CartResponse ajouterArticle(String email, CartItemRequest request) {
        Cart cart = trouverPanierParEmail(email);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Produit", request.getProductId()));

        if (!product.isActif()) {
            throw new BusinessException("Ce produit n'est plus disponible");
        }

        // Vérifier le stock
        if (product.getStock() < request.getQuantite()) {
            throw new BusinessException("Stock insuffisant. Disponible: " + product.getStock());
        }

        // Vérifier si l'article existe déjà dans le panier
        CartItem itemExistant = cart.getLignes().stream()
                .filter(i -> i.getProduct().getId().equals(request.getProductId())
                        && (request.getVariantId() == null
                            || (i.getVariant() != null && i.getVariant().getId().equals(request.getVariantId()))))
                .findFirst()
                .orElse(null);

        if (itemExistant != null) {
            // Mettre à jour la quantité
            int nouvelleQuantite = itemExistant.getQuantite() + request.getQuantite();
            if (product.getStock() < nouvelleQuantite) {
                throw new BusinessException("Stock insuffisant pour cette quantité");
            }
            itemExistant.setQuantite(nouvelleQuantite);
        } else {
            // Nouvel article
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantite(request.getQuantite())
                    .build();

            cart.getLignes().add(newItem);
        }

        cart.setDateModification(LocalDateTime.now());
        cartRepository.save(cart);
        return convertirEnResponse(cart);
    }

    // Modifier la quantité d'un article
    @Transactional
    public CartResponse modifierQuantite(String email, Long itemId, Integer quantite) {
        Cart cart = trouverPanierParEmail(email);

        CartItem item = cart.getLignes().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Article", itemId));

        if (quantite <= 0) {
            cart.getLignes().remove(item);
        } else {
            // Vérifier le stock
            if (item.getProduct().getStock() < quantite) {
                throw new BusinessException("Stock insuffisant");
            }
            item.setQuantite(quantite);
        }

        cart.setDateModification(LocalDateTime.now());
        cartRepository.save(cart);
        return convertirEnResponse(cart);
    }

    // Retirer un article du panier
    @Transactional
    public CartResponse retirerArticle(String email, Long itemId) {
        Cart cart = trouverPanierParEmail(email);

        cart.getLignes().removeIf(i -> i.getId().equals(itemId));
        cart.setDateModification(LocalDateTime.now());
        cartRepository.save(cart);
        return convertirEnResponse(cart);
    }

    // Appliquer un code promo
    @Transactional
    public CartResponse appliquerCoupon(String email, String code) {
        Cart cart = trouverPanierParEmail(email);

        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new BusinessException("Code promo invalide: " + code));

        if (!coupon.isActif()) {
            throw new BusinessException("Ce code promo n'est plus actif");
        }

        if (coupon.getDateExpiration() != null && coupon.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Ce code promo a expiré");
        }

        if (coupon.getUsagesActuels() >= coupon.getUsagesMax()) {
            throw new BusinessException("Ce code promo a atteint sa limite d'utilisation");
        }

        cart.setCodeCoupon(code);
        cartRepository.save(cart);
        return convertirEnResponse(cart);
    }

    // Retirer le coupon
    @Transactional
    public CartResponse retirerCoupon(String email) {
        Cart cart = trouverPanierParEmail(email);
        cart.setCodeCoupon(null);
        cartRepository.save(cart);
        return convertirEnResponse(cart);
    }

    // =====================
    // Méthodes utilitaires
    // =====================

    private Cart trouverPanierParEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
        return cartRepository.findByCustomerId(user.getId())
                .orElseThrow(() -> new BusinessException("Panier non trouvé"));
    }

    private CartResponse convertirEnResponse(Cart cart) {
        // Calculer le sous-total
        double sousTotal = cart.getLignes().stream()
                .mapToDouble(item -> {
                    double prix = item.getProduct().getPrixPromo() != null
                            ? item.getProduct().getPrixPromo()
                            : item.getProduct().getPrix();
                    if (item.getVariant() != null) {
                        prix += item.getVariant().getPrixDelta();
                    }
                    return prix * item.getQuantite();
                })
                .sum();

        // Calculer la remise du coupon
        double remise = 0;
        if (cart.getCodeCoupon() != null) {
            Coupon coupon = couponRepository.findByCode(cart.getCodeCoupon()).orElse(null);
            if (coupon != null && coupon.isActif()) {
                if (coupon.getType() == CouponType.PERCENT) {
                    remise = sousTotal * (coupon.getValeur() / 100);
                } else {
                    remise = coupon.getValeur();
                }
            }
        }

        // Frais de livraison (gratuit au-dessus du seuil)
        double fraisLivraison = (sousTotal - remise) >= LIVRAISON_GRATUITE_SEUIL ? 0 : FRAIS_LIVRAISON;
        double totalTTC = sousTotal - remise + fraisLivraison;

        // Convertir les lignes
        List<CartResponse.CartItemResponse> lignes = cart.getLignes().stream()
                .map(item -> {
                    double prix = item.getProduct().getPrixPromo() != null
                            ? item.getProduct().getPrixPromo()
                            : item.getProduct().getPrix();
                    if (item.getVariant() != null) prix += item.getVariant().getPrixDelta();

                    String variantInfo = item.getVariant() != null
                            ? item.getVariant().getAttribut() + ": " + item.getVariant().getValeur()
                            : null;

                    String image = item.getProduct().getImages().isEmpty()
                            ? null : item.getProduct().getImages().get(0);

                    return CartResponse.CartItemResponse.builder()
                            .id(item.getId())
                            .productId(item.getProduct().getId())
                            .productNom(item.getProduct().getNom())
                            .productImage(image)
                            .prixUnitaire(prix)
                            .quantite(item.getQuantite())
                            .sousTotal(prix * item.getQuantite())
                            .variantId(item.getVariant() != null ? item.getVariant().getId() : null)
                            .variantInfo(variantInfo)
                            .build();
                })
                .collect(Collectors.toList());

        return CartResponse.builder()
                .id(cart.getId())
                .lignes(lignes)
                .codeCoupon(cart.getCodeCoupon())
                .sousTotal(sousTotal)
                .remise(remise)
                .fraisLivraison(fraisLivraison)
                .totalTTC(Math.max(0, totalTTC))
                .build();
    }
}
