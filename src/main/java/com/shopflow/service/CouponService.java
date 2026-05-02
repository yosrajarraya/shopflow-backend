package com.shopflow.service;

import com.shopflow.dto.request.CouponRequest;
import com.shopflow.entity.Coupon;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    @Transactional
    public Coupon creerCoupon(CouponRequest request) {
        if (couponRepository.existsByCode(request.getCode())) {
            throw new BusinessException("Un coupon avec ce code existe déjà");
        }

        Coupon coupon = Coupon.builder()
                .code(request.getCode().toUpperCase())
                .type(request.getType())
                .valeur(request.getValeur())
                .dateExpiration(request.getDateExpiration())
                .usagesMax(request.getUsagesMax())
                .build();

        return couponRepository.save(coupon);
    }

    @Transactional
    public Coupon modifierCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon", id));

        coupon.setValeur(request.getValeur());
        coupon.setDateExpiration(request.getDateExpiration());
        coupon.setUsagesMax(request.getUsagesMax());
        return couponRepository.save(coupon);
    }

    @Transactional
    public void supprimerCoupon(Long id) {
        couponRepository.deleteById(id);
    }

    // Vérifier la validité d'un coupon
    public Coupon validerCoupon(String code) {
        Coupon coupon = couponRepository.findByCode(code.toUpperCase())
                .orElseThrow(() -> new BusinessException("Code promo invalide"));

        if (!coupon.isActif()) {
            throw new BusinessException("Ce coupon n'est plus actif");
        }

        if (coupon.getDateExpiration() != null && coupon.getDateExpiration().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Ce coupon a expiré");
        }

        if (coupon.getUsagesActuels() >= coupon.getUsagesMax()) {
            throw new BusinessException("Ce coupon a atteint sa limite d'utilisation");
        }

        return coupon;
    }
}
