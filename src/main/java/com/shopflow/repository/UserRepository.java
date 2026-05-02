package com.shopflow.repository;

import com.shopflow.entity.User;
import com.shopflow.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Trouver un utilisateur par email (pour login)
    Optional<User> findByEmail(String email);

    // Vérifier si un email existe déjà (pour l'inscription)
    boolean existsByEmail(String email);

    // Trouver tous les utilisateurs par rôle
    List<User> findByRole(Role role);

    // Trouver les utilisateurs actifs
    List<User> findByActif(boolean actif);
}
