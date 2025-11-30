package com.technical.config.jwt.service;

import com.technical.config.jwt.entity.JwtToken;

public interface JwtTokenService {
  JwtToken findByToken(String token);
  void saveJwtToken(String token, String userName);
  void invalidateJwtToken(String token);
}
