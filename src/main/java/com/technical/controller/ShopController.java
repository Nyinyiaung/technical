package com.technical.controller;

import com.technical.entity.Shop;
import com.technical.service.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    @GetMapping
    public ResponseEntity<List<Shop>> getAllShops() {
        List<Shop> shops = shopService.getAllShops();
        return ResponseEntity.ok(shops);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Shop> getShopById(@PathVariable Long id) {
        return shopService.getShopById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Shop> getShopByName(@PathVariable String name) {
        return shopService.getShopByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Shop> getShopByEmail(@PathVariable String email) {
        return shopService.getShopByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Shop>> searchShops(@RequestParam String keyword) {
        List<Shop> shops = shopService.searchShops(keyword);
        return ResponseEntity.ok(shops);
    }

    @GetMapping("/province/{provinceId}")
    public ResponseEntity<List<Shop>> getShopsByProvince(@PathVariable Integer provinceId) {
        List<Shop> shops = shopService.getShopsByProvince(provinceId);
        return ResponseEntity.ok(shops);
    }

    @GetMapping("/district/{districtId}")
    public ResponseEntity<List<Shop>> getShopsByDistrict(@PathVariable Integer districtId) {
        List<Shop> shops = shopService.getShopsByDistrict(districtId);
        return ResponseEntity.ok(shops);
    }

    @PostMapping
    public ResponseEntity<Shop> createShop(@Valid @RequestBody Shop shop) {
        try {
            Shop createdShop = shopService.createShop(shop);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdShop);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Shop> updateShop(@PathVariable Long id, @Valid @RequestBody Shop shop) {
        try {
            Shop updatedShop = shopService.updateShop(id, shop);
            return ResponseEntity.ok(updatedShop);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteShop(@PathVariable Long id) {
        try {
            shopService.deleteShop(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkShopExists(@PathVariable Long id) {
        boolean exists = shopService.shopExists(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/name/{name}/exists")
    public ResponseEntity<Boolean> checkShopExistsByName(@PathVariable String name) {
        boolean exists = shopService.shopExistsByName(name);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/email/{email}/exists")
    public ResponseEntity<Boolean> checkShopExistsByEmail(@PathVariable String email) {
        boolean exists = shopService.shopExistsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
