package com.technical.service;

import com.technical.dao.ShopRepository;
import com.technical.entity.Shop;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopRepository shopRepository;

    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    public Optional<Shop> getShopById(Long id) {
        return shopRepository.findById(id);
    }

    public Optional<Shop> getShopByName(String name) {
        return shopRepository.findByName(name);
    }

    public Optional<Shop> getShopByEmail(String email) {
        return shopRepository.findByEmail(email);
    }

    public List<Shop> searchShops(String keyword) {
        return shopRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Shop> getShopsByProvince(Integer provinceId) {
        return shopRepository.findByProvinceId(provinceId);
    }

    public List<Shop> getShopsByDistrict(Integer districtId) {
        return shopRepository.findByDistrictId(districtId);
    }

    public Shop createShop(Shop shop) {
        if (shopRepository.existsByName(shop.getName())) {
            throw new IllegalArgumentException("Shop with name '" + shop.getName() + "' already exists");
        }
        if (shop.getEmail() != null && shopRepository.existsByEmail(shop.getEmail())) {
            throw new IllegalArgumentException("Shop with email '" + shop.getEmail() + "' already exists");
        }
        return shopRepository.save(shop);
    }

    public Shop updateShop(Long id, Shop shopDetails) {
        return shopRepository.findById(id)
                .map(shop -> {
                    shop.setName(shopDetails.getName());
                    shop.setImage(shopDetails.getImage());
                    shop.setPhone(shopDetails.getPhone());
                    shop.setEmail(shopDetails.getEmail());
                    shop.setRemark(shopDetails.getRemark());
                    shop.setLocation(shopDetails.getLocation());
                    shop.setProvinceId(shopDetails.getProvinceId());
                    shop.setDistrictId(shopDetails.getDistrictId());
                    shop.setSubDistrictId(shopDetails.getSubDistrictId());
                    shop.setPostalCode(shopDetails.getPostalCode());
                    shop.setAlley(shopDetails.getAlley());
                    shop.setRoad(shopDetails.getRoad());
                    shop.setVillageNo(shopDetails.getVillageNo());
                    shop.setAddressNo(shopDetails.getAddressNo());
                    return shopRepository.save(shop);
                })
                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + id));
    }

    public void deleteShop(Long id) {
        if (!shopRepository.existsById(id)) {
            throw new RuntimeException("Shop not found with id: " + id);
        }
        shopRepository.deleteById(id);
    }

    public boolean shopExists(Long id) {
        return shopRepository.existsById(id);
    }

    public boolean shopExistsByName(String name) {
        return shopRepository.existsByName(name);
    }

    public boolean shopExistsByEmail(String email) {
        return shopRepository.existsByEmail(email);
    }
}
