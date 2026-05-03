package com.shopflow.service;

import com.shopflow.dto.request.OrderRequest;
import com.shopflow.dto.response.OrderResponse;
import com.shopflow.entity.*;
import com.shopflow.enums.OrderStatus;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final CouponRepository couponRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartService cartService;

    // Passer une commande depuis le panier
    @Transactional
    public OrderResponse passerCommande(String email, OrderRequest request) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BusinessException("Panier non trouvé"));

        if (cart.getLignes().isEmpty()) {
            throw new BusinessException("Votre panier est vide");
        }

        // Vérification finale du stock pour chaque article
        for (CartItem item : cart.getLignes()) {
            Product product = item.getProduct();
            if (item.getVariant() != null) {
                if (item.getVariant().getStockSupplementaire() < item.getQuantite()) {
                    throw new BusinessException("Stock variante insuffisant pour: " + product.getNom());
                }
            } else if (product.getStock() < item.getQuantite()) {
                throw new BusinessException("Stock insuffisant pour: " + product.getNom()
                        + " (disponible: " + product.getStock() + ")");
            }
        }

        // Calculer les totaux (utiliser la logique du CartService)
        var panierResponse = cartService.voirPanier(email);
        double sousTotal = panierResponse.getSousTotal();
        double fraisLivraison = panierResponse.getFraisLivraison();
        double totalTTC = panierResponse.getTotalTTC();

        String adresseLivraison;
        if (request.getAddressId() != null) {
            // Utiliser une adresse enregistrée
            adresseLivraison = addressRepository.findById(request.getAddressId())
                    .map(a -> a.getRue() + ", " + a.getVille() + " " + a.getCodePostal() + ", " + a.getPays())
                    .orElseThrow(() -> new BusinessException("Adresse introuvable"));
        } else if (request.getAdresseLivraison() != null && !request.getAdresseLivraison().isBlank()) {
            adresseLivraison = request.getAdresseLivraison();
        } else {
            throw new BusinessException("Veuillez fournir une adresse de livraison");
        }

        // Créer la commande
        Order order = Order.builder()
                .customer(customer)
                .numeroCommande(genererNumeroCommande())
                .adresseLivraison(adresseLivraison)
                .notes(request.getNotes())
                .sousTotal(sousTotal)
                .fraisLivraison(fraisLivraison)
                .totalTTC(totalTTC)
                .build();

        // Créer les lignes de commande — SANS déduire le stock (le stock sera déduit à l'acceptation du vendeur)
        List<OrderItem> lignes = cart.getLignes().stream().map(cartItem -> {
            Product product = cartItem.getProduct();

            // NE PAS déduire le stock ici — seulement à l'acceptation vendeur
            // NE PAS incrémenter nombreVentes ici

            double prixUnitaire = product.getPrixPromo() != null
                    ? product.getPrixPromo() : product.getPrix();
            if (cartItem.getVariant() != null) {
                prixUnitaire += cartItem.getVariant().getPrixDelta();
            }

            return OrderItem.builder()
                    .order(order)
                    .product(product)
                    .variant(cartItem.getVariant())
                    .quantite(cartItem.getQuantite())
                    .prixUnitaire(prixUnitaire)
                    .build();
        }).collect(Collectors.toList());

        order.setLignes(lignes);
        orderRepository.save(order);

        if (cart.getCodeCoupon() != null) {
            couponRepository.findByCode(cart.getCodeCoupon()).ifPresent(coupon -> {
                coupon.setUsagesActuels(coupon.getUsagesActuels() + 1);
                couponRepository.save(coupon);
            });
        }

        // Vider le panier après la commande
        cart.getLignes().clear();
        cart.setCodeCoupon(null);
        cartRepository.save(cart);

        log.info("Commande créée: {} pour {}", order.getNumeroCommande(), email);
        return convertirEnResponse(order);
    }

    // Voir une commande
    public OrderResponse voirCommande(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));

        // Vérifier que c'est la commande du bon utilisateur (ou admin/seller)
        User user = userRepository.findByEmail(email).orElseThrow();
        boolean estAdmin = user.getRole().name().equals("ADMIN");
        boolean estProprietaire = order.getCustomer().getEmail().equals(email);

        if (!estAdmin && !estProprietaire) {
            throw new BusinessException("Accès refusé à cette commande");
        }

        return convertirEnResponse(order);
    }

    // Mes commandes (client)
    public List<OrderResponse> mesCommandes(String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        return orderRepository.findByCustomerIdOrderByDateCommandeDesc(customer.getId())
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // Mes commandes (vendeur) - basées sur les produits du vendeur
    public List<OrderResponse> mesCommandesVendeur(String email) {
        User seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        // Récupérer toutes les commandes et filtrer celles contenant les produits du vendeur
        return orderRepository.findAllByOrderByDateCommandeDesc(org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .filter(order -> order.getLignes().stream()
                        .anyMatch(item -> item.getProduct() != null 
                                && item.getProduct().getSeller() != null 
                                && item.getProduct().getSeller().getId().equals(seller.getId())))
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // Mettre à jour le statut (vendeur/admin)
    @Transactional
    public OrderResponse mettreAJourStatut(Long id, OrderStatus nouveauStatut, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
        boolean estAdmin = user.getRole().name().equals("ADMIN");
        if (!estAdmin && !estCommandeDuVendeur(order, email)) {
            throw new BusinessException("Vous ne pouvez modifier que les commandes de vos produits");
        }

        order.setStatut(nouveauStatut);
        order.setNew(true); // Notifier le client
        orderRepository.save(order);

        log.info("Statut commande {} -> {}", order.getNumeroCommande(), nouveauStatut);
        return convertirEnResponse(order);
    }

    // Décision vendeur: accepter/refuser et notifier le client
    @Transactional
    public OrderResponse decisionCommandeVendeur(Long id, String action, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        boolean estAdmin = user.getRole().name().equals("ADMIN");
        if (!estAdmin && !estCommandeDuVendeur(order, email)) {
            throw new BusinessException("Vous ne pouvez traiter que les commandes de vos produits");
        }

        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.PAID) {
            throw new BusinessException("Cette commande ne peut plus être traitée (statut: " + order.getStatut() + ")");
        }

        String normalizedAction = action == null ? "" : action.trim().toUpperCase();
        if ("ACCEPT".equals(normalizedAction)) {
            // Acceptation → déduire le stock maintenant
            order.getLignes().forEach(item -> {
                Product product = item.getProduct();
                if (item.getVariant() != null) {
                    ProductVariant variant = item.getVariant();
                    if (variant.getStockSupplementaire() < item.getQuantite()) {
                        throw new BusinessException("Stock variante insuffisant pour: " + product.getNom());
                    }
                    variant.setStockSupplementaire(variant.getStockSupplementaire() - item.getQuantite());
                    productVariantRepository.save(variant);
                } else {
                    if (product.getStock() < item.getQuantite()) {
                        throw new BusinessException("Stock insuffisant pour: " + product.getNom()
                                + " (disponible: " + product.getStock() + ")");
                    }
                    product.setStock(product.getStock() - item.getQuantite());
                }
                product.setNombreVentes(product.getNombreVentes() + item.getQuantite());
                productRepository.save(product);
            });
            order.setStatut(OrderStatus.PROCESSING);
        } else if ("REFUSE".equals(normalizedAction)) {
            // Refus → stock non touché (il n'avait pas été déduit)
            order.setStatut(OrderStatus.CANCELLED);
        } else {
            throw new BusinessException("Action invalide. Utilisez ACCEPT ou REFUSE");
        }

        order.setNew(true); // le client verra la décision sur sa commande
        orderRepository.save(order);

        log.info("Décision vendeur sur {} : {}", order.getNumeroCommande(), normalizedAction);
        return convertirEnResponse(order);
    }

    // Annuler une commande (client)
    @Transactional
    public OrderResponse annulerCommande(Long id, String email) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commande", id));

        if (!order.getCustomer().getEmail().equals(email)) {
            throw new BusinessException("Vous ne pouvez pas annuler cette commande");
        }

        // On ne peut annuler que PENDING ou PAID
        if (order.getStatut() != OrderStatus.PENDING && order.getStatut() != OrderStatus.PAID) {
            throw new BusinessException("Cette commande ne peut plus être annulée (statut: " + order.getStatut() + ")");
        }

        // Stock non touché car il n'a été déduit qu'à l'acceptation vendeur (PROCESSING)
        // Si la commande est PENDING ou PAID, le vendeur n'a pas encore accepté → stock intact
        // Si la commande est PROCESSING ou plus, l'annulation n'est plus possible (géré ci-dessus)

        order.setStatut(OrderStatus.CANCELLED);
        orderRepository.save(order);

        log.info("Commande annulée: {}", order.getNumeroCommande());
        return convertirEnResponse(order);
    }

    // Toutes les commandes (admin)
    public List<OrderResponse> toutesLesCommandes() {
        return orderRepository.findAllByOrderByDateCommandeDesc(
                org.springframework.data.domain.Pageable.unpaged())
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // =====================
    // Méthodes privées
    // =====================

    private synchronized String genererNumeroCommande() {
        String annee = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy"));
        String prefix = "ORD-" + annee + "-";
        Long dernierNumero = orderRepository.findMaxNumeroCommandeSuffixByPrefix(prefix);
        long prochainNumero = (dernierNumero == null ? 0L : dernierNumero) + 1L;
        return String.format("%s%05d", prefix, prochainNumero);
    }

    private boolean estCommandeDuVendeur(Order order, String emailVendeur) {
        return order.getLignes().stream().anyMatch(item ->
                item.getProduct() != null
                        && item.getProduct().getSeller() != null
                        && emailVendeur.equalsIgnoreCase(item.getProduct().getSeller().getEmail()));
    }

    private OrderResponse convertirEnResponse(Order order) {
        List<OrderResponse.OrderItemResponse> lignes = order.getLignes().stream()
                .map(item -> {
                    String variantInfo = item.getVariant() != null
                            ? item.getVariant().getAttribut() + ": " + item.getVariant().getValeur()
                            : null;
                    String productImage = (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty())
                            ? item.getProduct().getImages().get(0)
                            : null;
                    return OrderResponse.OrderItemResponse.builder()
                            .id(item.getId())
                            .productId(item.getProduct().getId())
                            .productNom(item.getProduct().getNom())
                            .productImage(productImage)
                            .quantite(item.getQuantite())
                            .prixUnitaire(item.getPrixUnitaire())
                            .sousTotal(item.getPrixUnitaire() * item.getQuantite())
                            .variantInfo(variantInfo)
                            .build();
                })
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .numeroCommande(order.getNumeroCommande())
                .statut(order.getStatut())
                .adresseLivraison(order.getAdresseLivraison())
                .notes(order.getNotes())
                .sousTotal(order.getSousTotal())
                .fraisLivraison(order.getFraisLivraison())
                .totalTTC(order.getTotalTTC())
                .dateCommande(order.getDateCommande())
                .isNew(order.isNew())
                .customerId(order.getCustomer().getId())
                .customerNom(order.getCustomer().getPrenom() + " " + order.getCustomer().getNom())
                .lignes(lignes)
                .build();
    }
}
