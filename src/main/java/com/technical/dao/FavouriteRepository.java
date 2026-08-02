package com.technical.dao;

import com.technical.entity.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {
    
    Optional<Favourite> findByUser_IdAndProduct_ProductId(Long userId, Long productId);
    
    @Query("SELECT f FROM Favourite f WHERE f.user.firstName = :userId ORDER BY f.createdTime DESC")
    List<Favourite> findUserFavourites(@Param("userId") String userId);
}
