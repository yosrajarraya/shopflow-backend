package com.shopflow.service;

import com.shopflow.dto.request.AddressRequest;
import com.shopflow.dto.response.AddressResponse;
import com.shopflow.entity.Address;
import com.shopflow.entity.User;
import com.shopflow.exception.BusinessException;
import com.shopflow.exception.ResourceNotFoundException;
import com.shopflow.repository.AddressRepository;
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
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressResponse> mesAdresses(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
        return addressRepository.findByUserId(user.getId()).stream()
                .map(this::convertirEnResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse ajouterAdresse(String email, AddressRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));

        // Si principale, désactiver les autres
        if (request.isPrincipal()) {
            addressRepository.findByUserId(user.getId()).forEach(a -> {
                a.setPrincipal(false);
                addressRepository.save(a);
            });
        }

        // Première adresse = principale automatiquement
        boolean estPrincipale = request.isPrincipal() || addressRepository.countByUserId(user.getId()) == 0;

        Address address = Address.builder()
                .user(user)
                .rue(request.getRue())
                .ville(request.getVille())
                .codePostal(request.getCodePostal())
                .pays(request.getPays())
                .principal(estPrincipale)
                .build();

        addressRepository.save(address);
        log.info("Adresse ajoutée pour: {}", email);
        return convertirEnResponse(address);
    }

    @Transactional
    public AddressResponse modifierAdresse(Long id, String email, AddressRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", id));
        if (!address.getUser().getId().equals(user.getId()))
            throw new BusinessException("Cette adresse ne vous appartient pas");

        if (request.isPrincipal()) {
            addressRepository.findByUserId(user.getId()).forEach(a -> {
                a.setPrincipal(false);
                addressRepository.save(a);
            });
        }

        address.setRue(request.getRue());
        address.setVille(request.getVille());
        address.setCodePostal(request.getCodePostal());
        address.setPays(request.getPays());
        address.setPrincipal(request.isPrincipal());
        addressRepository.save(address);
        return convertirEnResponse(address);
    }

    @Transactional
    public void supprimerAdresse(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", id));
        if (!address.getUser().getId().equals(user.getId()))
            throw new BusinessException("Cette adresse ne vous appartient pas");
        addressRepository.delete(address);
    }

    @Transactional
    public AddressResponse definirPrincipale(Long id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Utilisateur non trouvé"));
        addressRepository.findByUserId(user.getId()).forEach(a -> {
            a.setPrincipal(false);
            addressRepository.save(a);
        });
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Adresse", id));
        if (!address.getUser().getId().equals(user.getId()))
            throw new BusinessException("Cette adresse ne vous appartient pas");
        address.setPrincipal(true);
        addressRepository.save(address);
        return convertirEnResponse(address);
    }

    private AddressResponse convertirEnResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .rue(a.getRue())
                .ville(a.getVille())
                .codePostal(a.getCodePostal())
                .pays(a.getPays())
                .principal(a.isPrincipal())
                .build();
    }
}
