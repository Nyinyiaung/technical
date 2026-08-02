package com.technical.service;

import com.technical.commonutil.UserUtil;
import com.technical.dao.FavouriteRepository;
import com.technical.dao.ProductRepository;
import com.technical.dao.UserRepository;
import com.technical.entity.Favourite;
import com.technical.entity.Product;
import com.technical.entity.user.User;
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
public class FavouriteService {

    private final FavouriteRepository favouriteRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public List<Favourite> getUserFavourites(String userId) {
        return favouriteRepository.findUserFavourites(userId);
    }

    public void addToFavourites(Long productId) {
        Long userId = UserUtil.getCurrentUserId();

        if (userId == null) {
            throw new RuntimeException("User not found");
        }

        if (productId == null) {
            throw new RuntimeException("Product not found");
        }

        // Fetch user from database
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));

        // Check if already in favourites
        Optional<Favourite> existingFavourite = favouriteRepository.findByUser_IdAndProduct_ProductId(userId, productId);
        if (existingFavourite.isPresent()) {
            log.info("Product already in favourites.");
            return;
        }

        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setProduct(product);

        favouriteRepository.save(favourite);
    }

    public void deleteFavourite(Long id) {
        if (!favouriteRepository.existsById(id)) {
            throw new RuntimeException("Favourite not found with id: " + id);
        }
        favouriteRepository.deleteById(id);
    }

    public void clearUserFavourites(String userId) {
        List<Favourite> userFavourites = favouriteRepository.findUserFavourites(userId);
        favouriteRepository.deleteAll(userFavourites);
    }
}
