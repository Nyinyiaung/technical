package com.technical.config.jwt.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.technical.config.jwt.entity.JwtToken;
import com.technical.config.jwt.repo.JwtTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements JwtTokenService {
  private final JwtTokenRepository repository;
  
  @Override
  public JwtToken findByToken(String token) {
    Optional<JwtToken> jwtTokenRedis = repository.findById(token);
      return jwtTokenRedis.orElse(null);
  }

  @Override
  public void saveJwtToken(String token, String userName) {
    repository.save(
        JwtToken.builder()
            .token(token)
            .valid(1)
            .userName(userName)
            .build()); 
  }

  @Override
  public void invalidateJwtToken(String token) {
    JwtToken jwtToken = findByToken(token);
    if (jwtToken != null) repository.delete(jwtToken);
  }
}
