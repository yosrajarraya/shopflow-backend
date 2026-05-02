package com.shopflow.service;

import com.shopflow.dto.request.CategoryRequest;
import com.shopflow.dto.response.CategoryResponse;
import com.shopflow.entity.Category;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    // Arbre de toutes les catégories (avec sous-catégories)
    @Transactional(readOnly = true)
    public List<CategoryResponse> listerCategories() {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(this::convertirAvecSousCategories)
                .collect(Collectors.toList());
    }

    @Transactional
    public CategoryResponse creerCategorie(CategoryRequest request) {
        Category category = new Category();
        category.setNom(request.getNom());
        category.setDescription(request.getDescription());

        if (request.getParentId() != null) {
            Category parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Catégorie parent", request.getParentId()));
            category.setParent(parent);
        }

        categoryRepository.save(category);
        return convertirEnResponse(category);
    }

    @Transactional
    public CategoryResponse modifierCategorie(Long id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Catégorie", id));

        category.setNom(request.getNom());
        category.setDescription(request.getDescription());
        categoryRepository.save(category);
        return convertirEnResponse(category);
    }

    @Transactional
    public void supprimerCategorie(Long id) {
        categoryRepository.deleteById(id);
    }

    // Conversion avec sous-catégories (récursif)
    private CategoryResponse convertirAvecSousCategories(Category c) {
        List<CategoryResponse> sous = c.getSousCategories().stream()
                .map(this::convertirAvecSousCategories)
                .collect(Collectors.toList());

        return CategoryResponse.builder()
                .id(c.getId())
                .nom(c.getNom())
                .description(c.getDescription())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .sousCategories(sous)
                .build();
    }

    private CategoryResponse convertirEnResponse(Category c) {
        return CategoryResponse.builder()
                .id(c.getId())
                .nom(c.getNom())
                .description(c.getDescription())
                .parentId(c.getParent() != null ? c.getParent().getId() : null)
                .build();
    }
}
