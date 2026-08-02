package com.technical.controller;

import com.technical.entity.Product;
import com.technical.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Product>> getProductsByStatus(@PathVariable String status) {
        List<Product> products = productService.getProductsByStatus(status);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Product>> getProductsByShop(@PathVariable Long shopId) {
        List<Product> products = productService.getProductsByShop(shopId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Product>> getAvailableProducts() {
        List<Product> products = productService.getAvailableProducts();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/available/category/{categoryId}")
    public ResponseEntity<List<Product>> getAvailableProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getAvailableProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/available/shop/{shopId}")
    public ResponseEntity<List<Product>> getAvailableProductsByShop(@PathVariable Long shopId) {
        List<Product> products = productService.getAvailableProductsByShop(shopId);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/available/search")
    public ResponseEntity<List<Product>> searchAvailableProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchAvailableProducts(keyword);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product) {
        try {
            Product createdProduct = productService.createProduct(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<Product> updateProductQuantity(@PathVariable Long id, @RequestParam Integer quantity) {
        try {
            Product updatedProduct = productService.updateProductQuantity(id, quantity);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Product> updateProductStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            Product updatedProduct = productService.updateProductStatus(id, status);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> checkProductExists(@PathVariable Long id) {
        boolean exists = productService.productExists(id);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/{id}/available")
    public ResponseEntity<Boolean> checkProductAvailable(@PathVariable Long id) {
        boolean available = productService.isProductAvailable(id);
        return ResponseEntity.ok(available);
    }
}
