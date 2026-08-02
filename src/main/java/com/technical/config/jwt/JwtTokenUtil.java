package com.technical.config.jwt;

import com.technical.commonutil.CommonUtil;
import com.technical.commonutil.DateUtil;
import com.technical.config.jwt.entity.JwtToken;
import com.technical.config.jwt.service.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class JwtTokenUtil {
	@Value("${jwt.token.login.expiration}")
	private String loginTokenExpiration;

	@Value("${jwt.token.reset.expiration}")
	private String resetTokenExpiration;

	@Value("${jwt.token.verification.expiration}")
	private String verificationTokenExpiration;

	@Value("${jwt.token.default.expiration}")
	private String defaultTokenExpiration;

	@Value("${jwt.token.refresh.expiration}")
	private String refreshTokenExpiration;

	@Value("${jwt.secret}")
	private String secret;

	private final JwtTokenService jwtTokenService;

	// retrieve email from jwt token
	public String getEmailFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	// retrieve userId from jwt token
	public Long getUserIdFromToken(String token) {
		return getClaimFromToken(token, claims -> claims.get("userId", Long.class));
	}

	// retrieve expiration date from jwt token
	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private SecretKey getSigningKey() {
		return Keys.hmacShaKeyFor(java.util.Base64.getDecoder().decode(secret));
	}

	// for retrieving any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts.parserBuilder()
				.setSigningKey(getSigningKey())
				.build()
				.parseClaimsJws(token)
				.getBody();
	}

	// check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// validate token with email
    public boolean validateToken(String token, String email) {
		JwtToken jwtToken = jwtTokenService.findByToken(token);
		if (jwtToken == null) return false;

        final String extractedEmail = getEmailFromToken(token);
        return (extractedEmail.equals(email) && !isTokenExpired(token));
    }

    // validate token without email check (for password reset)
    public boolean validateToken(String token) {
		JwtToken jwtToken = jwtTokenService.findByToken(token);
		if (jwtToken == null) return false;

		return !isTokenExpired(token);
    }

	public void deleteToken(String token) {
		jwtTokenService.invalidateJwtToken(token);
	}

	// generate token for user
	public String generateToken(String userName, Long userId, String tokenType) {
		Date timeout = getExpirationForTokenType(tokenType);
		String token = Jwts.builder()
				.setSubject(userName)
				.claim("userId", userId)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(timeout)
				.signWith(getSigningKey(), SignatureAlgorithm.HS256)
				.compact();
		jwtTokenService.saveJwtToken(token, userName, tokenType, DateUtil.getMillis(timeout));
		return token;
	}

	/**
	 * Return the expiration date for a given token type.
	 * The expiration date is determined by the value of the corresponding environment variable.
	 * If the environment variable is not set, the default expiration date is used.
	 *
	 * @param tokenType the type of the token (LOGIN, RESET, or VERIFICATION)
	 * @return the expiration date for the given token type
	 */
	private Date getExpirationForTokenType(String tokenType) {
		String expirationInMinutes = switch (tokenType) {
			case CommonUtil.ACCESS_TOKEN_TYPE -> loginTokenExpiration;
			case CommonUtil.RESET_TOKEN_TYPE -> resetTokenExpiration;
			case CommonUtil.VERIFICATION_TOKEN_TYPE -> verificationTokenExpiration;
			case CommonUtil.REFRESH_TOKEN_TYPE -> refreshTokenExpiration;
			default -> defaultTokenExpiration;
		};

		return DateUtil.addMinutes(Integer.valueOf(expirationInMinutes));
	}
}
