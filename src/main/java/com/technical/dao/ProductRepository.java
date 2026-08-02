package com.technical.dao;

import com.technical.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByStatus(String status);
    
    List<Product> findByCategory_CategoryId(Long categoryId);
    
    List<Product> findByShop_ShopId(Long shopId);
    
    @Query("SELECT p FROM Product p WHERE p.quantity > 0")
    List<Product> findAvailableProducts();
    
    @Query("SELECT p FROM Product p WHERE p.category.categoryId = :categoryId AND p.quantity > 0")
    List<Product> findAvailableProductsByCategory(@Param("categoryId") Long categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.shop.shopId = :shopId AND p.quantity > 0")
    List<Product> findAvailableProductsByShop(@Param("shopId") Long shopId);
    
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% AND p.quantity > 0")
    List<Product> searchAvailableProducts(@Param("name") String name);
}
