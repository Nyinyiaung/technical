package com.technical.controller;

import com.technical.entity.Favourite;
import com.technical.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Favourite>> getUserFavourites(@PathVariable String userId) {
        List<Favourite> favourites = favouriteService.getUserFavourites(userId);
        return ResponseEntity.ok(favourites);
    }

    @PostMapping("/product/{productId}")
    public ResponseEntity<Void> addToFavourites(@PathVariable Long productId) {
        favouriteService.addToFavourites(productId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFavourite(@PathVariable Long id) {
        favouriteService.deleteFavourite(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> clearUserFavourites(@PathVariable String userId) {
        favouriteService.clearUserFavourites(userId);
        return ResponseEntity.noContent().build();
    }
}
