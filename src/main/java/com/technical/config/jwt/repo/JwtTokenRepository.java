package com.technical.config.jwt.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.technical.config.jwt.entity.JwtToken;

@Repository
public interface JwtTokenRepository extends CrudRepository<JwtToken, String> {
}
