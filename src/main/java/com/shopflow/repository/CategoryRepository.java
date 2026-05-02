package com.shopflow.repository;

import com.shopflow.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Récupérer les catégories racines (sans parent)
    List<Category> findByParentIsNull();
}
