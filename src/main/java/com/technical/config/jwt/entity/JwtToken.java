package com.technical.config.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "JwtToken", timeToLive = 3600)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {

	@Id
	private String token;
	private Integer valid; // 1 - Valid, 2 - Invalid
	private String userName;
}
