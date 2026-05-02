package com.shopflow.entity;

import com.shopflow.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String prenom;
    private String nom;
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder.Default
    private boolean actif = true;

    @Builder.Default
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Un utilisateur peut avoir plusieurs adresses
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Address> adresses = new ArrayList<>();

    // Un vendeur a un profil boutique
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SellerProfile sellerProfile;

    // Un client a un panier
    @OneToOne(mappedBy = "customer", cascade = CascadeType.ALL)
    private Cart cart;
}
