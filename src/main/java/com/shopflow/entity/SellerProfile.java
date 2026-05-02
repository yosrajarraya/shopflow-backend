package com.shopflow.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seller_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relation 1-1 avec User
    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(nullable = false)
    private String nomBoutique;

    private String description;
    private String logo;

    @Builder.Default
    private Double note = 0.0;
}
