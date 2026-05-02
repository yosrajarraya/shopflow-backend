-- Ajouter les produits supplémentaires (sans spécifier les IDs)

INSERT INTO categories (nom, description, parent_id) VALUES
('Hommes Suppl', 'Vêtements pour hommes supplémentaires', NULL),
('Femmes Suppl', 'Vêtements pour femmes supplémentaires', NULL);

INSERT INTO products (nom, description, prix, stock, actif, seller_id, date_creation, nombre_ventes) VALUES
('Laptop Dell XPS 13', 'Ordinateur portable ultra-fin 13 pouces, Intel i7, 16GB RAM, 512GB SSD', 999.99, 25, 1, 2, NOW(), 45),
('Clavier Gaming Corsair', 'Clavier mécanique RGB, switches Cherry MX, programmable', 159.99, 50, 1, 2, NOW(), 120),
('Moniteur LG 27"', 'Écran 27" 4K IPS, 60Hz, dalle mate, ajustable', 349.99, 15, 1, 2, NOW(), 32),
('Casque Sony WH-1000XM5', 'Casque sans fil avec réduction de bruit active', 379.99, 20, 1, 2, NOW(), 88),
('Robe Été Fleurie', 'Robe légère pour l\'été, motifs floraux', 49.99, 90, 1, 2, NOW(), 140),
('Veste Cuir Noir', 'Veste en cuir véritable noir, intemporelle', 149.99, 40, 1, 2, NOW(), 75),
('Blender Smoothie Pro', 'Blender haute puissance 2000W, 5 vitesses', 199.99, 35, 1, 2, NOW(), 80),
('Poêle Antiadhésive Titanium', 'Poêle 28cm avec revêtement titanium durable', 39.99, 150, 1, 2, NOW(), 220),
('Haltères Ajustables', 'Set de 2 haltères ajustables 1-10kg', 299.99, 30, 1, 2, NOW(), 92),
('Tapis Yoga Premium', 'Tapis yoga 6mm épaisseur, antidérapant', 44.99, 100, 1, 2, NOW(), 280);

INSERT INTO users (email, nom, prenom, mot_de_passe, role, actif, date_creation) VALUES
('marie@example.com', 'Dupont', 'Marie', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'CUSTOMER', 1, NOW()),
('pierre@example.com', 'Martin', 'Pierre', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'CUSTOMER', 1, NOW()),
('sophie@example.com', 'Bernard', 'Sophie', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'CUSTOMER', 1, NOW()),
('jean.seller@example.com', 'Lefevre', 'Jean', '$2a$10$slYQmyNdGzin7olVN3p5Be7DhH2qJ3j4y5D2Z5q8q8uq8uq8uq8uq', 'SELLER', 1, NOW());

INSERT INTO addresses (user_id, rue, ville, code_postal, pays, principal) VALUES
(3, '45 Avenue des Champs', 'Marseille', '13000', 'France', 1),
(4, '78 Boulevard Saint-Germain', 'Lyon', '69000', 'France', 1),
(4, '10 Rue de la Liberté', 'Villeurbanne', '69100', 'France', 0);

INSERT INTO orders (customer_id, numero_commande, statut, sous_total, frais_livraison, totalttc, adresse_livraison, date_commande, is_new) VALUES
(3, 'CMD-001', 'DELIVERED', 249.97, 9.99, 259.96, '45 Avenue des Champs, Marseille', DATE_SUB(NOW(), INTERVAL 30 DAY), 1),
(4, 'CMD-002', 'SHIPPED', 399.97, 15.99, 415.96, '78 Boulevard Saint-Germain, Lyon', DATE_SUB(NOW(), INTERVAL 15 DAY), 1),
(3, 'CMD-003', 'PROCESSING', 199.98, 9.99, 209.97, '45 Avenue des Champs, Marseille', DATE_SUB(NOW(), INTERVAL 5 DAY), 0),
(4, 'CMD-004', 'PAID', 129.99, 10, 139.99, '78 Boulevard Saint-Germain, Lyon', NOW(), 1);

INSERT INTO reviews (product_id, customer_id, note, commentaire, approuve, date_creation) VALUES
(1, 3, 5, 'Excellent tissu, très confortable et bien taillé!', 1, DATE_SUB(NOW(), INTERVAL 25 DAY)),
(2, 4, 4, 'Très bon jeans, arrivé rapidement', 1, DATE_SUB(NOW(), INTERVAL 20 DAY)),
(5, 3, 5, 'Clavier excellent, RGB magnifique', 1, DATE_SUB(NOW(), INTERVAL 18 DAY)),
(3, 4, 4, 'Chaussures très agréables à porter', 1, DATE_SUB(NOW(), INTERVAL 10 DAY));

-- Vérification
SELECT COUNT(*) as Total_Produits FROM products;
SELECT COUNT(*) as Total_Utilisateurs FROM users;
SELECT COUNT(*) as Total_Commandes FROM orders;
SELECT COUNT(*) as Total_Reviews FROM reviews;
