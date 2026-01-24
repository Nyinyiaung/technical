package com.technical.config.jwt.service;

import com.technical.config.jwt.entity.JwtToken;
import com.technical.config.jwt.repo.JwtTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
  public void saveJwtToken(String token, String userName, String tokenType, long timeout) {
    JwtToken jwtToken = JwtToken.builder()
            .token(token)
            .userName(userName)
            .tokenType(tokenType)
            .valid(1)
            .timeToLive(timeout)
            .build();
    repository.save(jwtToken);
  }

  @Override
  public void invalidateJwtToken(String token) {
    JwtToken jwtToken = findByToken(token);
    if (jwtToken != null) repository.delete(jwtToken);
    SecurityContextHolder.clearContext();
  }
}
