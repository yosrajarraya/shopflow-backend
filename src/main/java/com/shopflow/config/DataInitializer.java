package com.shopflow.config;

import com.shopflow.entity.*;
import com.shopflow.enums.CouponType;
import com.shopflow.enums.OrderStatus;
import com.shopflow.enums.Role;
import com.shopflow.repository.AddressRepository;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.CategoryRepository;
import com.shopflow.repository.CouponRepository;
import com.shopflow.repository.OrderItemRepository;
import com.shopflow.repository.OrderRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.ReviewRepository;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initData(
            UserRepository userRepo,
            CategoryRepository categoryRepo,
            ProductRepository productRepo,
            CartRepository cartRepo,
            CouponRepository couponRepo,
            AddressRepository addressRepo,
            OrderRepository orderRepo,
            OrderItemRepository orderItemRepo,
            ReviewRepository reviewRepo,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // ✅ TOUJOURS recréer l'admin s'il n'existe pas
            if (!userRepo.existsByEmail("admin@shopflow.com")) {
                User admin = User.builder()
                        .email("admin@shopflow.com")
                        .motDePasse(passwordEncoder.encode("admin123"))
                        .prenom("Admin")
                        .nom("ShopFlow")
                        .role(Role.ADMIN)
                        .build();
                userRepo.save(admin);
                log.info("✅ Admin recréé: admin@shopflow.com / admin123");
            }

            // Éviter de réinsérer le reste des données si elles existent déjà
            if (userRepo.count() > 1) return;

            log.info("=== Initialisation des données de démonstration ===");

            // ---- Créer les autres utilisateurs ----
            User vendeur = User.builder()
                    .email("vendeur@shopflow.com")
                    .motDePasse(passwordEncoder.encode("vendeur123"))
                    .prenom("Ahmed")
                    .nom("Ben Ali")
                    .role(Role.SELLER)
                    .build();

            SellerProfile profil = SellerProfile.builder()
                    .user(vendeur)
                    .nomBoutique("Tech Tunisie")
                    .description("Spécialiste en électronique et informatique")
                    .note(4.8)
                    .build();
            vendeur.setSellerProfile(profil);

            User client1 = User.builder()
                    .email("client@shopflow.com")
                    .motDePasse(passwordEncoder.encode("client123"))
                    .prenom("Sarra")
                    .nom("Chaabane")
                    .role(Role.CUSTOMER)
                    .build();

            User client2 = User.builder()
                    .email("marie.dupont@example.com")
                    .motDePasse(passwordEncoder.encode("password123"))
                    .prenom("Marie")
                    .nom("Dupont")
                    .role(Role.CUSTOMER)
                    .build();

            User client3 = User.builder()
                    .email("pierre.martin@example.com")
                    .motDePasse(passwordEncoder.encode("password123"))
                    .prenom("Pierre")
                    .nom("Martin")
                    .role(Role.CUSTOMER)
                    .build();

            User vendeur2 = User.builder()
                    .email("fashion@shopflow.com")
                    .motDePasse(passwordEncoder.encode("vendeur123"))
                    .prenom("Zahra")
                    .nom("Abidi")
                    .role(Role.SELLER)
                    .build();

            SellerProfile profil2 = SellerProfile.builder()
                    .user(vendeur2)
                    .nomBoutique("Fashion Plus")
                    .description("Boutique de mode et accessoires")
                    .note(4.6)
                    .build();
            vendeur2.setSellerProfile(profil2);

            userRepo.saveAll(List.of(vendeur, client1, client2, client3, vendeur2));

            // ---- Adresses des clients ----
            Address adresse1 = Address.builder()
                    .user(client1)
                    .rue("12 Rue de la République")
                    .ville("Tunis")
                    .codePostal("1000")
                    .pays("Tunisie")
                    .principal(true)
                    .build();

            Address adresse2 = Address.builder()
                    .user(client2)
                    .rue("25 Avenue des Champs")
                    .ville("Paris")
                    .codePostal("75008")
                    .pays("France")
                    .principal(true)
                    .build();

            Address adresse3 = Address.builder()
                    .user(client3)
                    .rue("5 Rue Lyon")
                    .ville("Lyon")
                    .codePostal("69000")
                    .pays("France")
                    .principal(true)
                    .build();

            addressRepo.saveAll(List.of(adresse1, adresse2, adresse3));

            // ---- Paniers des clients ----
            Cart cart1 = Cart.builder().customer(client1).build();
            Cart cart2 = Cart.builder().customer(client2).build();
            Cart cart3 = Cart.builder().customer(client3).build();
            cartRepo.saveAll(List.of(cart1, cart2, cart3));

            // ---- Catégories ----
            Category electronique = Category.builder()
                    .nom("Électronique").description("Produits électroniques").build();
            Category informatique = Category.builder()
                    .nom("Informatique").description("Ordinateurs et accessoires").build();
            Category telephones = Category.builder()
                    .nom("Téléphones").description("Smartphones et accessoires")
                    .parent(electronique).build();
            Category mode = Category.builder()
                    .nom("Mode").description("Vêtements et accessoires").build();
            Category hommes = Category.builder()
                    .nom("Vêtements Hommes").description("Vêtements pour hommes")
                    .parent(mode).build();
            Category femmes = Category.builder()
                    .nom("Vêtements Femmes").description("Vêtements pour femmes")
                    .parent(mode).build();
            Category maison = Category.builder()
                    .nom("Maison").description("Décoration et mobilier").build();

            categoryRepo.saveAll(List.of(electronique, informatique, telephones, mode, hommes, femmes, maison));

            // ---- Produits Informatique ----
            Product laptop = Product.builder()
                    .seller(vendeur)
                    .nom("Laptop Dell Inspiron 15")
                    .description("Ordinateur portable performant, Intel Core i5, 8Go RAM, 256Go SSD, écran 15.6 FHD")
                    .prix(1299.99)
                    .prixPromo(999.99)
                    .stock(10)
                    .nombreVentes(45)
                    .categories(Set.of(informatique))
                    .images(List.of("https://via.placeholder.com/300x300?text=Laptop+Dell"))
                    .build();

            Product clavier = Product.builder()
                    .seller(vendeur)
                    .nom("Clavier Mécanique RGB")
                    .description("Clavier gaming mécanique, switches Cherry MX, rétroéclairage RGB programmable")
                    .prix(159.99)
                    .prixPromo(129.99)
                    .stock(30)
                    .nombreVentes(120)
                    .categories(Set.of(informatique))
                    .images(List.of("https://via.placeholder.com/300x300?text=Clavier+Mech"))
                    .build();

            Product souris = Product.builder()
                    .seller(vendeur)
                    .nom("Souris Gaming Wireless")
                    .description("Souris sans fil haute précision 4K, batterie rechargeable 20h, poids léger")
                    .prix(79.99)
                    .stock(50)
                    .nombreVentes(250)
                    .categories(Set.of(informatique))
                    .images(List.of("https://via.placeholder.com/300x300?text=Souris"))
                    .build();

            Product phone = Product.builder()
                    .seller(vendeur)
                    .nom("Samsung Galaxy A54")
                    .description("Smartphone Android 13, 128Go, écran AMOLED 6.4 pouces, appareil photo 50MP")
                    .prix(699.00)
                    .stock(25)
                    .nombreVentes(88)
                    .categories(Set.of(telephones, electronique))
                    .images(List.of("https://via.placeholder.com/300x300?text=Samsung+A54"))
                    .build();

            Product ecouteurs = Product.builder()
                    .seller(vendeur)
                    .nom("Écouteurs Bluetooth JBL")
                    .description("Écouteurs sans fil, autonomie 20h, réduction active de bruit, étanche IP65")
                    .prix(149.00)
                    .stock(50)
                    .nombreVentes(180)
                    .categories(Set.of(electronique))
                    .images(List.of("https://via.placeholder.com/300x300?text=JBL+Earbuds"))
                    .build();

            Product casque = Product.builder()
                    .seller(vendeur)
                    .nom("Casque Sony WH-1000XM5")
                    .description("Casque supra-auriculaire sans fil avec réduction de bruit lieder-class")
                    .prix(379.99)
                    .prixPromo(329.99)
                    .stock(15)
                    .nombreVentes(95)
                    .categories(Set.of(electronique))
                    .images(List.of("https://via.placeholder.com/300x300?text=Sony+Headset"))
                    .build();

            Product tshirt = Product.builder()
                    .seller(vendeur2)
                    .nom("T-Shirt Coton Premium Blanc")
                    .description("T-shirt en coton 100% biologique, confortable et durable")
                    .prix(34.99)
                    .prixPromo(24.99)
                    .stock(100)
                    .nombreVentes(450)
                    .categories(Set.of(hommes, mode))
                    .images(List.of("https://via.placeholder.com/300x300?text=TShirt"))
                    .build();

            Product jeans = Product.builder()
                    .seller(vendeur2)
                    .nom("Pantalon Denim Slim Bleu")
                    .description("Jean slim fit moderne, confortable et tendance")
                    .prix(79.99)
                    .prixPromo(59.99)
                    .stock(60)
                    .nombreVentes(220)
                    .categories(Set.of(hommes, mode))
                    .images(List.of("https://via.placeholder.com/300x300?text=Jeans"))
                    .build();

            Product veste = Product.builder()
                    .seller(vendeur2)
                    .nom("Veste Bomber Noir")
                    .description("Veste bomber moderne en polyester résistant, poches latérales")
                    .prix(119.99)
                    .prixPromo(89.99)
                    .stock(40)
                    .nombreVentes(110)
                    .categories(Set.of(hommes, mode))
                    .images(List.of("https://via.placeholder.com/300x300?text=Bomber"))
                    .build();

            Product robe = Product.builder()
                    .seller(vendeur2)
                    .nom("Robe Été Fleurie")
                    .description("Robe légère et confortable pour l'été, motifs floraux colorés")
                    .prix(69.99)
                    .prixPromo(49.99)
                    .stock(35)
                    .nombreVentes(140)
                    .categories(Set.of(femmes, mode))
                    .images(List.of("https://via.placeholder.com/300x300?text=Robe"))
                    .build();

            Product vestefemme = Product.builder()
                    .seller(vendeur2)
                    .nom("Veste en Cuir Noir")
                    .description("Veste en cuir véritable noir, intemporelle et élégante")
                    .prix(199.99)
                    .prixPromo(149.99)
                    .stock(20)
                    .nombreVentes(75)
                    .categories(Set.of(femmes, mode))
                    .images(List.of("https://via.placeholder.com/300x300?text=Leather+Jacket"))
                    .build();

            Product lampe = Product.builder()
                    .seller(vendeur)
                    .nom("Lampe LED Design Minimaliste")
                    .description("Lampe de table minimaliste avec LED intégrée, variation de luminosité")
                    .prix(49.99)
                    .prixPromo(34.99)
                    .stock(40)
                    .nombreVentes(165)
                    .categories(Set.of(maison))
                    .images(List.of("https://via.placeholder.com/300x300?text=Lamp"))
                    .build();

            Product tapis = Product.builder()
                    .seller(vendeur)
                    .nom("Tapis Persan 200x300")
                    .description("Tapis traditionnel 200x300cm, couleurs chaudes, motifs artisanaux")
                    .prix(299.99)
                    .prixPromo(199.99)
                    .stock(8)
                    .nombreVentes(28)
                    .categories(Set.of(maison))
                    .images(List.of("https://via.placeholder.com/300x300?text=Rug"))
                    .build();

            productRepo.saveAll(List.of(laptop, clavier, souris, phone, ecouteurs, casque,
                    tshirt, jeans, veste, robe, vestefemme, lampe, tapis));

            // ---- Coupons ----
            Coupon coupon1 = Coupon.builder()
                    .code("BIENVENUE10")
                    .type(CouponType.PERCENT)
                    .valeur(10.0)
                    .dateExpiration(LocalDateTime.now().plusMonths(3))
                    .usagesMax(200)
                    .build();

            Coupon coupon2 = Coupon.builder()
                    .code("SOLDE25")
                    .type(CouponType.FIXED)
                    .valeur(25.0)
                    .dateExpiration(LocalDateTime.now().plusMonths(1))
                    .usagesMax(100)
                    .build();

            couponRepo.saveAll(List.of(coupon1, coupon2));

            // ---- Commandes ----
            Order order1 = Order.builder()
                    .customer(client1)
                    .numeroCommande("CMD-2026-001")
                    .statut(OrderStatus.DELIVERED)
                    .sousTotal(299.97)
                    .fraisLivraison(9.99)
                    .totalTTC(309.96)
                    .adresseLivraison("12 Rue de la République, Tunis")
                    .dateCommande(LocalDateTime.now().minusDays(30))
                    .build();

            Order order2 = Order.builder()
                    .customer(client2)
                    .numeroCommande("CMD-2026-002")
                    .statut(OrderStatus.SHIPPED)
                    .sousTotal(1109.97)
                    .fraisLivraison(15.99)
                    .totalTTC(1125.96)
                    .adresseLivraison("25 Avenue des Champs, Paris")
                    .dateCommande(LocalDateTime.now().minusDays(15))
                    .build();

            Order order3 = Order.builder()
                    .customer(client3)
                    .numeroCommande("CMD-2026-003")
                    .statut(OrderStatus.PROCESSING)
                    .sousTotal(449.97)
                    .fraisLivraison(10.0)
                    .totalTTC(459.97)
                    .adresseLivraison("5 Rue Lyon, Lyon")
                    .dateCommande(LocalDateTime.now().minusDays(5))
                    .build();

            orderRepo.saveAll(List.of(order1, order2, order3));

            // ---- Articles de commande ----
            OrderItem oi1 = OrderItem.builder()
                    .order(order1).product(tshirt).quantite(2).prixUnitaire(24.99).build();
            OrderItem oi2 = OrderItem.builder()
                    .order(order1).product(jeans).quantite(1).prixUnitaire(59.99).build();
            OrderItem oi3 = OrderItem.builder()
                    .order(order2).product(laptop).quantite(1).prixUnitaire(999.99).build();
            OrderItem oi4 = OrderItem.builder()
                    .order(order2).product(clavier).quantite(1).prixUnitaire(129.99).build();
            OrderItem oi5 = OrderItem.builder()
                    .order(order3).product(phone).quantite(1).prixUnitaire(699.00).build();

            orderItemRepo.saveAll(List.of(oi1, oi2, oi3, oi4, oi5));

            // ---- Avis ----
            Review rev1 = Review.builder().product(tshirt).customer(client1).note(5)
                    .commentaire("Excellent tissu, très confortable!").approuve(true)
                    .dateCreation(LocalDateTime.now().minusDays(25)).build();
            Review rev2 = Review.builder().product(jeans).customer(client2).note(4)
                    .commentaire("Très bon jeans, arrivé rapidement").approuve(true)
                    .dateCreation(LocalDateTime.now().minusDays(20)).build();
            Review rev3 = Review.builder().product(clavier).customer(client1).note(5)
                    .commentaire("Clavier excellent, RGB magnifique").approuve(true)
                    .dateCreation(LocalDateTime.now().minusDays(18)).build();
            Review rev4 = Review.builder().product(phone).customer(client3).note(4)
                    .commentaire("Téléphone très agréable, caméra excellente").approuve(true)
                    .dateCreation(LocalDateTime.now().minusDays(10)).build();
            Review rev5 = Review.builder().product(lampe).customer(client2).note(5)
                    .commentaire("Lampe élégante, luminosité parfaite").approuve(true)
                    .dateCreation(LocalDateTime.now().minusDays(8)).build();

            reviewRepo.saveAll(List.of(rev1, rev2, rev3, rev4, rev5));

            log.info("=== Données chargées avec succès ! ===");
            log.info("  Admin:   admin@shopflow.com / admin123");
            log.info("  Vendeur: vendeur@shopflow.com / vendeur123");
            log.info("  Client:  client@shopflow.com / client123");
        };
    }
}