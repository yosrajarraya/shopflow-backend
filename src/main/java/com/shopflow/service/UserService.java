package com.shopflow.service;

import com.shopflow.dto.response.UserResponse;
import com.shopflow.entity.User;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    // Lister tous les utilisateurs
    public List<UserResponse> listerUtilisateurs() {
        return userRepository.findAll()
                .stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    // Obtenir un utilisateur
    public UserResponse obtenirUtilisateur(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));
        return convertirEnResponse(user);
    }

    // Activer un compte
    @Transactional
    public UserResponse activerCompte(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));

        if (user.isActif()) {
            throw new BusinessException("Ce compte est déjà actif");
        }

        user.setActif(true);
        User updated = userRepository.save(user);
        log.info("Compte activé: {}", id);
        return convertirEnResponse(updated);
    }

    // Désactiver un compte
    @Transactional
    public UserResponse desactiverCompte(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));

        if (!user.isActif()) {
            throw new BusinessException("Ce compte est déjà désactivé");
        }

        user.setActif(false);
        User updated = userRepository.save(user);
        log.info("Compte désactivé: {}", id);
        return convertirEnResponse(updated);
    }

    // Modifier un utilisateur (nom, prénom, email, téléphone, rôle)
    @Transactional
    public UserResponse modifierUtilisateur(Long id, String nom, String prenom, String email, String telephone, String role) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));

        // Vérifier unicité email si changé
        if (!user.getEmail().equalsIgnoreCase(email) && userRepository.existsByEmail(email)) {
            throw new BusinessException("Cet email est déjà utilisé par un autre compte");
        }

        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setTelephone(telephone);
        try {
            user.setRole(com.shopflow.enums.Role.valueOf(role));
        } catch (IllegalArgumentException e) {
            throw new BusinessException("Rôle invalide : " + role);
        }

        User updated = userRepository.save(user);
        log.info("Utilisateur modifié: {}", id);
        return convertirEnResponse(updated);
    }

    // Supprimer un utilisateur
    @Transactional
    public void supprimerUtilisateur(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", id));

        userRepository.delete(user);
        log.info("Utilisateur supprimé: {}", id);
    }

    // Convertir une entité en DTO
    private UserResponse convertirEnResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .email(user.getEmail())
                .telephone(user.getTelephone())
                .role(user.getRole().name())
                .actif(user.isActif())
                .dateCreation(user.getDateCreation().toString())
                .build();
    }
}
