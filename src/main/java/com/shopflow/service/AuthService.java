package com.shopflow.service;

import com.shopflow.dto.request.LoginRequest;
import com.shopflow.dto.request.RegisterRequest;
import com.shopflow.dto.response.AuthResponse;
import com.shopflow.entity.Cart;
import com.shopflow.entity.PasswordResetToken;
import com.shopflow.entity.SellerProfile;
import com.shopflow.entity.User;
import com.shopflow.enums.Role;
import com.shopflow.exception.BusinessException;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.PasswordResetTokenRepository;
import com.shopflow.repository.UserRepository;
import com.shopflow.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    // Inscription d'un client
    @Transactional
    public AuthResponse inscrireClient(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé");
        }

        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(Role.CUSTOMER)
                .build();

        userRepository.save(user);

        Cart cart = Cart.builder().customer(user).build();
        cartRepository.save(cart);

        log.info("Nouveau client inscrit: {}", user.getEmail());
        return genererAuthResponse(user);
    }

    // Inscription d'un vendeur
    @Transactional
    public AuthResponse inscrireVendeur(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé");
        }

        if (request.getNomBoutique() == null || request.getNomBoutique().isBlank()) {
            throw new BusinessException("Le nom de la boutique est obligatoire pour un vendeur");
        }

        User user = User.builder()
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .prenom(request.getPrenom())
                .nom(request.getNom())
                .role(Role.SELLER)
                .build();

        SellerProfile profile = SellerProfile.builder()
                .user(user)
                .nomBoutique(request.getNomBoutique())
                .description(request.getDescriptionBoutique())
                .build();

        user.setSellerProfile(profile);
        userRepository.save(user);

        log.info("Nouveau vendeur inscrit: {} - Boutique: {}", user.getEmail(), request.getNomBoutique());
        return genererAuthResponse(user);
    }

    // ✅ Connexion — vérification manuelle sans authenticationManager
    public AuthResponse connecter(LoginRequest request) {
        // 1. Chercher l'utilisateur par email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        // 2. Vérifier que le compte est actif
        if (!user.isActif()) {
            throw new BadCredentialsException("Compte désactivé");
        }

        // 3. Vérifier le mot de passe avec BCrypt
        if (!passwordEncoder.matches(request.getMotDePasse(), user.getMotDePasse())) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        log.info("Connexion réussie: {}", user.getEmail());
        return genererAuthResponse(user);
    }

    // Renouveler le token
    public AuthResponse rafraichirToken(String refreshToken) {
        String email = jwtService.extraireEmail(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        if (!jwtService.estValide(refreshToken, userDetails)) {
            throw new BusinessException("Refresh token invalide ou expiré");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        return genererAuthResponse(user);
    }

    @Transactional
    public String demanderReinitialisationMotDePasse(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Aucun compte trouvé avec cet email"));

        passwordResetTokenRepository.deleteByEmail(email);
        passwordResetTokenRepository.deleteByExpiresAtBefore(LocalDateTime.now());

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .email(user.getEmail())
                .expiresAt(LocalDateTime.now().plusMinutes(30))
                .build();

        passwordResetTokenRepository.save(resetToken);
        log.info("Token de réinitialisation généré pour {}", email);

        return token;
    }

    @Transactional
    public void reinitialiserMotDePasse(String token, String nouveauMotDePasse) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> new BusinessException("Token de réinitialisation invalide"));

        if (resetToken.isUsed()) {
            throw new BusinessException("Ce token a déjà été utilisé");
        }

        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Le token de réinitialisation est expiré");
        }

        User user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        user.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        userRepository.save(user);

        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);
        log.info("Mot de passe réinitialisé pour {}", user.getEmail());
    }

    // Méthode privée pour générer la réponse avec les deux tokens
    private AuthResponse genererAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.genererAccessToken(userDetails);
        String refreshToken = jwtService.genererRefreshToken(userDetails);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .build();
    }
}