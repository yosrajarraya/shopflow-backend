-- ==================== CATEGORIES ====================
INSERT INTO categories (nom, description, parent_id) VALUES
('Électronique', 'Tous les produits électroniques', NULL),
('Informatique', 'Ordinateurs et accessoires', 1),
('Téléphones', 'Smartphones et accessoires', 1),
('Mode', 'Vêtements et accessoires', NULL),
('Hommes', 'Vêtements pour hommes', 4),
('Femmes', 'Vêtements pour femmes', 4),
('Maison', 'Décoration et mobilier', NULL),
('Cuisine', 'Électroménager et accessoires cuisine', 7),
('Sports', 'Articles de sport', NULL),
('Fitness', 'Équipements de fitness', 9);

-- ==================== PRODUITS SUPPLEMENTAIRES ====================

-- Informatique
INSERT INTO products (nom, description, prix, stock, actif, seller_id, date_creation, nombre_ventes, prix_promo) VALUES
('Laptop Dell XPS 13', 'Ordinateur portable ultra-fin 13 pouces, Intel i7, 16GB RAM, 512GB SSD', 999.99, 25, 1, 2, NOW(), 45, 899.99),
('Clavier Gaming Corsair', 'Clavier mécanique RGB, switches Cherry MX, programmable', 159.99, 50, 1, 2, NOW(), 120, 129.99),
('Souris Logitech', 'Souris sans fil haute précision 4K, batterie rechargeable', 49.99, 100, 1, 2, NOW(), 250, 39.99),
('Moniteur LG 27"', 'Écran 27" 4K IPS, 60Hz, dalle mate, ajustable', 349.99, 15, 1, 2, NOW(), 32, 299.99),
('Casque Sony WH-1000XM5', 'Casque sans fil avec réduction de bruit active', 379.99, 20, 1, 2, NOW(), 88, 329.99);

-- Mode Hommes
INSERT INTO products (nom, description, prix, stock, actif, seller_id, date_creation, nombre_ventes, prix_promo) VALUES
('T-shirt Coton Premium', 'T-shirt en coton 100% biologique, confortable', 24.99, 200, 1, 2, NOW(), 450, 19.99),
('Pantalon Denim Slim', 'Jean slim fit à la mode, confortable', 59.99, 150, 1, 2, NOW(), 220, 49.99),
('Veste Bomber', 'Veste bomber moderne en polyester résistant', 89.99, 80, 1, 2, NOW(), 110, 74.99),
('Chaussures Sneaker Nike', 'Sneakers blanches confortables pour tous les jours', 99.99, 120, 1, 2, NOW(), 180, 79.99);

-- Mode Femmes
INSERT INTO products (nom, description, prix, stock, actif, seller_id, date_creation, nombre_ventes, prix_promo) VALUES
('Robe Été Fleurie', 'Robe légère pour l\'été, motifs floraux', 49.99, 90, 1, 2, NOW(), 140, 39.99),
('Veste Cuir Noir', 'Veste en cuir véritable noir, intemporelle', 149.99, 40, 1, 2, NOW(), 75, 119.99),
('Legging Fitness', 'Legging haute taille pour le sport', 39.99, 200, 1, 2, NOW(), 320, 29.99),
('Chaussures Talon Doré', 'Escarpins confortables avec talon 7cm', 79.99, 60, 1, 2, NOW(), 95, 59.99);

-- Maison
INSERT INTO products (nom, description, prix, stock, actif, seller_id, date_creation, nombre_ventes, prix_promo) VALUES
('Lampe LED Design', 'Lampe de table minimaliste avec LED intégrée', 34.99, 75, 1, 2, NOW(), 165, 27.99),
('Tapis Persan', 'Tapis traditionnel 200x300cm, couleurs chaudes', 199.99, 15, 1, 2, NOW(), 28, 149.99),
('Coussin Velours', 'Ensemble de 4 coussins en velours', 44.99, 120, 1, 2, NOW(), 250, 34.99);

-- Cuisine
INSERT INTO products (nom, description, prix, stock, actif, seller_id, date_creation, nombre_ventes, prix_promo) VALUES
('Blender Smoothie Pro', 'Blender haute puissance 2000W, 5 vitesses', 199.99, 35, 1, 2, NOW(), 80, 159.99),
('Poêle Antiadhésive Titanium', 'Poêle 28cm avec revêtement titanium durable', 39.99, 150, 1, 2, NOW(), 220, 29.99),
('Machine à Café Espresso', 'Machine espresso manuelle avec buse vapeur', 299.99, 20, 1, 2, NOW(), 45, 249.99);

-- Sports & Fitness
INSERT INTO products (nom, description, prix, stock, actif, seller_id, date_creation, nombre_ventes, prix_promo) VALUES
('Haltères Ajustables', 'Set de 2 haltères ajustables 1-10kg', 299.99, 30, 1, 2, NOW(), 92, 249.99),
('Tapis Yoga Premium', 'Tapis yoga 6mm épaisseur, antidérapant', 44.99, 100, 1, 2, NOW(), 280, 34.99),
('Vélo Elliptique', 'Machine elliptique avec 12 niveaux de résistance', 449.99, 10, 1, 2, NOW(), 18, 379.99),
('Panier Basketball', 'Panier professionnel hauteur réglable', 159.99, 25, 1, 2, NOW(), 55, 129.99);

-- ==================== VARIANTES PRODUITS ====================
INSERT INTO product_variants (product_id, attribut, valeur, stock_supplementaire, prix_delta) VALUES
(4, 'Couleur', 'Noir', 30, 0),
(4, 'Couleur', 'Argent', 25, 0),
(5, 'Taille', 'S', 40, 0),
(5, 'Taille', 'M', 50, 0),
(5, 'Taille', 'L', 45, 0),
(6, 'Couleur', 'Bleu', 60, 0),
(6, 'Couleur', 'Noir', 55, 0),
(10, 'Couleur', 'Noir', 80, 0),
(10, 'Couleur', 'Gris', 70, 0),
(10, 'Couleur', 'Bleu', 50, 0);

-- ==================== CATEGORIES POUR PRODUITS ====================
INSERT INTO product_categories (product_id, category_id) VALUES
(1, 2), (2, 2), (3, 2), (4, 2), (5, 2),
(6, 5), (7, 5), (8, 5), (9, 5),
(10, 6), (11, 6), (12, 6), (13, 6),
(14, 7), (15, 7), (16, 7),
(17, 8), (18, 8), (19, 8),
(20, 9), (21, 9), (22, 9), (23, 9);

-- ==================== IMAGES PRODUITS ====================
INSERT INTO product_images (product_id, image_url) VALUES
(1, 'https://via.placeholder.com/300x300?text=Dell+XPS'),
(2, 'https://via.placeholder.com/300x300?text=Corsair+Keyboard'),
(3, 'https://via.placeholder.com/300x300?text=Logitech+Mouse'),
(4, 'https://via.placeholder.com/300x300?text=LG+Monitor'),
(5, 'https://via.placeholder.com/300x300?text=Sony+Headset'),
(6, 'https://via.placeholder.com/300x300?text=T-shirt'),
(7, 'https://via.placeholder.com/300x300?text=Denim'),
(8, 'https://via.placeholder.com/300x300?text=Bomber'),
(9, 'https://via.placeholder.com/300x300?text=Nike+Shoes'),
(10, 'https://via.placeholder.com/300x300?text=Summer+Dress'),
(11, 'https://via.placeholder.com/300x300?text=Leather+Jacket'),
(12, 'https://via.placeholder.com/300x300?text=Leggings'),
(13, 'https://via.placeholder.com/300x300?text=Heels'),
(14, 'https://via.placeholder.com/300x300?text=Lamp'),
(15, 'https://via.placeholder.com/300x300?text=Rug'),
(16, 'https://via.placeholder.com/300x300?text=Cushion'),
(17, 'https://via.placeholder.com/300x300?text=Blender'),
(18, 'https://via.placeholder.com/300x300?text=Pan'),
(19, 'https://via.placeholder.com/300x300?text=Coffee+Machine'),
(20, 'https://via.placeholder.com/300x300?text=Dumbbells'),
(21, 'https://via.placeholder.com/300x300?text=Yoga+Mat'),
(22, 'https://via.placeholder.com/300x300?text=Elliptical'),
(23, 'https://via.placeholder.com/300x300?text=Basketball');

-- ==================== UTILISATEURS ADDITIONNELS ====================
INSERT INTO users (email, nom, prenom, mot_de_passe, role, actif, date_creation) VALUES
('marie@example.com', 'Dupont', 'Marie', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'CUSTOMER', 1, NOW()),
('pierre@example.com', 'Martin', 'Pierre', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'CUSTOMER', 1, NOW()),
('sophie@example.com', 'Bernard', 'Sophie', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'CUSTOMER', 1, NOW()),
('jean.seller@example.com', 'Lefevre', 'Jean', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'SELLER', 1, NOW());

-- ==================== PROFILS VENDEURS ====================
INSERT INTO seller_profiles (user_id, nom_boutique, description, logo, note) VALUES
(6, 'TechWorld Store', 'Spécialiste en électronique et informatique', 'https://via.placeholder.com/100x100?text=TechWorld', 4.8),
(7, 'Fashion Plus', 'Tendances mode pour tous les styles', 'https://via.placeholder.com/100x100?text=Fashion', 4.6);

-- ==================== ADRESSES ====================
INSERT INTO addresses (user_id, rue, ville, code_postal, pays, principal) VALUES
(1, '123 Rue de la Paix', 'Paris', '75001', 'France', 1),
(3, '45 Avenue des Champs', 'Marseille', '13000', 'France', 1),
(4, '78 Boulevard Saint-Germain', 'Lyon', '69000', 'France', 1),
(4, '10 Rue de la Liberté', 'Villeurbanne', '69100', 'France', 0);

-- ==================== COMMANDES ====================
INSERT INTO orders (customer_id, numero_commande, statut, sous_total, frais_livraison, totalttc, adresse_livraison, date_commande, is_new) VALUES
(3, 'CMD-001', 'DELIVERED', 249.97, 9.99, 259.96, '45 Avenue des Champs, Marseille', DATE_SUB(NOW(), INTERVAL 30 DAY), 1),
(4, 'CMD-002', 'SHIPPED', 399.97, 15.99, 415.96, '78 Boulevard Saint-Germain, Lyon', DATE_SUB(NOW(), INTERVAL 15 DAY), 1),
(3, 'CMD-003', 'PROCESSING', 199.98, 9.99, 209.97, '45 Avenue des Champs, Marseille', DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
(4, 'CMD-004', 'PAID', 129.99, 10, 139.99, '78 Boulevard Saint-Germain, Lyon', NOW(), 1);

-- ==================== ELEMENTS COMMANDES ====================
INSERT INTO order_items (order_id, product_id, quantite, prix_unitaire, variant_id) VALUES
(1, 6, 2, 24.99, NULL),
(1, 7, 1, 59.99, NULL),
(2, 2, 1, 159.99, NULL),
(2, 3, 1, 49.99, NULL),
(3, 4, 1, 199.99, NULL),
(4, 9, 1, 99.99, NULL);

-- ==================== AVIS/REVIEWS ====================
INSERT INTO reviews (product_id, customer_id, note, commentaire, approuve, date_creation) VALUES
(6, 3, 5, 'Excellent tissu, très confortable et bien taillé!', 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(7, 4, 4, 'Très bon jeans, arrivé rapidement', 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(2, 3, 5, 'Clavier excellent, RGB magnifique', 1, DATE_SUB(NOW(), INTERVAL 18 DAY)),
(9, 4, 4, 'Chaussures très agréables à porter', 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(21, 3, 5, 'Tapis parfait pour le yoga, anti-glisse génial', 1, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(3, 4, 5, 'Souris très réactive et confortable', 1, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- ==================== VERIFICATION ====================
SELECT COUNT(*) as Total_Produits FROM products;
SELECT COUNT(*) as Total_Utilisateurs FROM users;
SELECT COUNT(*) as Total_Commandes FROM orders;
SELECT COUNT(*) as Total_Reviews FROM reviews;
