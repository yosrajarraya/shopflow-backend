-- Script pour ajouter un administrateur par défaut
-- Email: admin@shopflow.com
-- Mot de passe en clair: admin123
-- Hash bcrypt: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/D1O

INSERT INTO users (email, nom, prenom, mot_de_passe, role, actif, date_creation, telephone) 
VALUES (
  'admin@shopflow.com',
  'Admin',
  'ShopFlow',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/D1O',
  'ADMIN',
  1,
  NOW(),
  '+216 1234 5678'
);

-- Vérification
SELECT email, nom, prenom, role, actif FROM users WHERE email = 'admin@shopflow.com';
