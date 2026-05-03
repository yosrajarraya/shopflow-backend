package com.shopflow.service;

import com.shopflow.dto.request.CouponRequest;
import com.shopflow.dto.response.CouponResponse;
import com.shopflow.entity.Coupon;
import com.shopflow.enums.CouponType;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;

    public List<CouponResponse> listerCoupons() {
        return couponRepository.findAll().stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public CouponResponse creerCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode().toUpperCase())) {
            throw new BusinessException("Un coupon avec ce code existe déjà");
        }
        // Convertir LocalDate → LocalDateTime (fin de journée)
        LocalDateTime expiration = request.getDateExpiration() != null
                ? request.getDateExpiration().atTime(23, 59, 59)
                : null;

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(CouponType.valueOf(request.getType()))
                .valeur(request.getValeur())
                .dateExpiration(expiration)
                .usagesMax(request.getUsagesMax() != null ? request.getUsagesMax() : 100)
                .usagesActuels(0)
                .actif(request.isActif())
                .build();
        couponRepository.save(coupon);
        log.info("Coupon créé: {}", coupon.getCode());
        return convertirEnResponse(coupon);
    }

    @Transactional
    public CouponResponse modifierCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));
        coupon.setType(CouponType.valueOf(request.getType()));
        coupon.setValeur(request.getValeur());
        // Convertir LocalDate → LocalDateTime
        LocalDateTime expiration = request.getDateExpiration() != null
                ? request.getDateExpiration().atTime(23, 59, 59)
                : null;
        coupon.setDateExpiration(expiration);
        if (request.getUsagesMax() != null) coupon.setUsagesMax(request.getUsagesMax());
        coupon.setActif(request.isActif());
        couponRepository.save(coupon);
        return convertirEnResponse(coupon);
    }

    @Transactional
    public void supprimerCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));
        couponRepository.delete(coupon);
        log.info("Coupon supprimé: {}", id);
    }

    public CouponResponse validerCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException("Code promo invalide: " + code));
        if (!coupon.isActif()) throw new BusinessException("Ce code promo n'est plus actif");
        if (coupon.getDateExpiration() != null && coupon.getDateExpiration().isBefore(LocalDateTime.now()))
            throw new BusinessException("Ce code promo a expiré");
        if (coupon.getUsagesActuels() >= coupon.getUsagesMax())
            throw new BusinessException("Ce code promo a atteint sa limite d'utilisation");
        return convertirEnResponse(coupon);
    }

    private CouponResponse convertirEnResponse(Coupon c) {
        return CouponResponse.builder()
                .id(c.getId())
                .code(c.getCode())
                .type(c.getType().name())
                .valeur(c.getValeur())
                .dateExpiration(c.getDateExpiration())
                .usagesMax(c.getUsagesMax())
                .usagesActuels(c.getUsagesActuels())
                .actif(c.isActif())
                .build();
    }
}
