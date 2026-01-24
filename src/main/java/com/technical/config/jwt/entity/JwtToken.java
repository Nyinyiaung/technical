package com.technical.config.jwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@RedisHash(value = "JwtToken")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class JwtToken {

	@Id
	private String token;
	private Integer valid; // 1 - Valid, 2 - Invalid
	private String userName;
	private String tokenType; // Store the token type (LOGIN, RESET, VERIFICATION)
	@TimeToLive
	private long timeToLive;
}
