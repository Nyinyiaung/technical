package com.technical.service;

import com.technical.dao.ProductRepository;
import com.technical.dao.CategoryRepository;
import com.technical.dao.ShopRepository;
import com.technical.entity.Product;
import com.technical.entity.Category;
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
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ShopRepository shopRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public List<Product> getProductsByStatus(String status) {
        return productRepository.findByStatus(status);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findByCategory_CategoryId(categoryId);
    }

    public List<Product> getProductsByShop(Long shopId) {
        return productRepository.findByShop_ShopId(shopId);
    }

    public List<Product> getAvailableProducts() {
        return productRepository.findAvailableProducts();
    }

    public List<Product> getAvailableProductsByCategory(Long categoryId) {
        return productRepository.findAvailableProductsByCategory(categoryId);
    }

    public List<Product> getAvailableProductsByShop(Long shopId) {
        return productRepository.findAvailableProductsByShop(shopId);
    }

    public List<Product> searchAvailableProducts(String keyword) {
        return productRepository.searchAvailableProducts(keyword);
    }

    public Product createProduct(Product product) {
        // Validate category exists
        if (product.getCategory() != null && product.getCategory().getCategoryId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + product.getCategory().getCategoryId()));
            product.setCategory(category);
        }

        // Validate shop exists
        if (product.getShop() != null && product.getShop().getShopId() != null) {
            Shop shop = shopRepository.findById(product.getShop().getShopId())
                    .orElseThrow(() -> new RuntimeException("Shop not found with id: " + product.getShop().getShopId()));
            product.setShop(shop);
        }

        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(productDetails.getName());
                    product.setDescription(productDetails.getDescription());
                    product.setImage(productDetails.getImage());
                    product.setStatus(productDetails.getStatus());
                    product.setQuantity(productDetails.getQuantity());

                    // Update category if provided
                    if (productDetails.getCategory() != null && productDetails.getCategory().getCategoryId() != null) {
                        Category category = categoryRepository.findById(productDetails.getCategory().getCategoryId())
                                .orElseThrow(() -> new RuntimeException("Category not found with id: " + productDetails.getCategory().getCategoryId()));
                        product.setCategory(category);
                    }

                    // Update shop if provided
                    if (productDetails.getShop() != null && productDetails.getShop().getShopId() != null) {
                        Shop shop = shopRepository.findById(productDetails.getShop().getShopId())
                                .orElseThrow(() -> new RuntimeException("Shop not found with id: " + productDetails.getShop().getShopId()));
                        product.setShop(shop);
                    }

                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product updateProductQuantity(Long id, Integer quantity) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setQuantity(quantity);
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product updateProductStatus(Long id, String status) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setStatus(status);
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    public boolean productExists(Long id) {
        return productRepository.existsById(id);
    }

    public boolean isProductAvailable(Long id) {
        return productRepository.findById(id)
                .map(product -> product.getQuantity() > 0)
                .orElse(false);
    }
}
