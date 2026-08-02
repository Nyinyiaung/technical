package com.technical.dao;

import com.technical.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {
    
    Optional<Shop> findByName(String name);
    
    Optional<Shop> findByEmail(String email);
    
    List<Shop> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT s FROM Shop s WHERE s.provinceId = :provinceId")
    List<Shop> findByProvinceId(@Param("provinceId") Integer provinceId);
    
    @Query("SELECT s FROM Shop s WHERE s.districtId = :districtId")
    List<Shop> findByDistrictId(@Param("districtId") Integer districtId);
    
    boolean existsByName(String name);
    
    boolean existsByEmail(String email);
}
