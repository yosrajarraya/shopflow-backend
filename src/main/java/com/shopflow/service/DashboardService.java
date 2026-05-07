package com.shopflow.service;

import com.shopflow.entity.User;
import com.shopflow.exception.BusinessException;
import com.shopflow.repository.OrderRepository;
import com.shopflow.repository.ProductRepository;
// import com.shopflow.service.ProductService;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

        private final OrderRepository orderRepository;
        private final ProductRepository productRepository;
        private final UserRepository userRepository;
        private final ProductService productService;
    

    // Dashboard ADMIN
    public Map<String, Object> dashboardAdmin() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("chiffreAffaires", orderRepository.calculerChiffreAffaires());
        stats.put("totalCommandes", orderRepository.count());
        stats.put("totalProduits", productRepository.count());
        stats.put("totalClients", userRepository.count());

        // Top 5 meilleures ventes
        // Top produits: utiliser le ProductService pour obtenir le DTO complet attendu par le frontend
        stats.put("topProduits", productService.topVentes().stream().limit(5).toList());

        // Commandes récentes (avec informations client et noms de champs attendus par le frontend)
        stats.put("commandesRecentes", orderRepository
                .findAllByOrderByDateCommandeDesc(PageRequest.of(0, 5))
                .stream()
                .map(this::toOrderSummaryMap)
                .toList());

        return stats;
    }

    // Dashboard SELLER
    public Map<String, Object> dashboardVendeur(String email) {
        User vendeur = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Vendeur non trouvé"));

        Map<String, Object> stats = new HashMap<>();

        stats.put("commandesEnAttente", orderRepository.compterCommandesEnAttente(vendeur.getId()));
        stats.put("totalProduits", productRepository.findBySellerId(
                vendeur.getId(), PageRequest.of(0, 1)).getTotalElements());

        // Revenu du mois pour le vendeur (dernier 30 jours)
        java.time.LocalDateTime since = java.time.LocalDateTime.now().minusDays(30);
        Double revenu = orderRepository.calculerRevenuDepuisPourVendeur(vendeur.getId(), since);
        stats.put("revenuMois", revenu != null ? revenu : 0.0);

        // Commandes récentes pour le vendeur (dernières 10 au lieu de 5)
        stats.put("commandesRecentes", orderRepository
                .findBySellerIdOrderByDateCommandeDesc(vendeur.getId(), PageRequest.of(0, 10))
                .stream()
                .map(this::toOrderSummaryMap)
                .toList());

        return stats;
    }

        private Map<String, Object> toOrderSummaryMap(com.shopflow.entity.Order o) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", o.getId());
                item.put("numeroCommande", o.getNumeroCommande());
                item.put("customerNom", o.getCustomer() != null ? (o.getCustomer().getPrenom() + " " + o.getCustomer().getNom()) : null);
                item.put("dateCommande", o.getDateCommande());
                item.put("totalTTC", o.getTotalTTC());
                item.put("statut", o.getStatut());

                // Lignes de commande (produits commandés)
                var lignes = o.getLignes().stream().map(l -> {
                        Map<String, Object> ligne = new HashMap<>();
                        ligne.put("productId", l.getProduct().getId());
                        ligne.put("productNom", l.getProduct().getNom());
                        ligne.put("quantite", l.getQuantite());
                        ligne.put("prixUnitaire", l.getPrixUnitaire());
                        ligne.put("sousTotal", l.getPrixUnitaire() * l.getQuantite());
                        // Image du produit (première image disponible)
                        String image = (l.getProduct().getImages() != null && !l.getProduct().getImages().isEmpty())
                                ? l.getProduct().getImages().get(0)
                                : null;
                        ligne.put("productImage", image);
                        return ligne;
                }).toList();
                item.put("lignes", lignes);

                return item;
        }

    // Dashboard CUSTOMER
    public Map<String, Object> dashboardCustomer(String email) {
        User customer = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Client non trouvé"));

        Map<String, Object> stats = new HashMap<>();

        // Commandes du client
        stats.put("totalCommandes", orderRepository.countByCustomerId(customer.getId()));
        
        // Commandes récentes (dernières 5)
        stats.put("commandesRecentes", orderRepository
                .findByCustomerIdOrderByDateCommandeDesc(customer.getId())
                .stream()
                .limit(5)
                .map(o -> Map.of(
                        "id", o.getId(),
                        "numero", o.getNumeroCommande(),
                        "statut", o.getStatut(),
                        "total", o.getTotalTTC(),
                        "date", o.getDateCommande()))
                .toList());

        return stats;
    }

    // Ventes par produit vs stock (pour le chart combo)
    public List<Map<String, Object>> getProductSalesVsStock(int limit) {
        return productRepository
                .findAllByActifTrueOrderByNombreVentesDesc(PageRequest.of(0, limit))
                .stream()
                .map(p -> {
                        Map<String, Object> item = new HashMap<>();
                        item.put("nom", p.getNom().length() > 20 ? p.getNom().substring(0, 20) + "..." : p.getNom());
                        item.put("nombreVentes", p.getNombreVentes() != null ? p.getNombreVentes() : 0);
                        item.put("stock", p.getStock() != null ? p.getStock() : 0);
                        return item;
                })
                .toList();
    }
}
