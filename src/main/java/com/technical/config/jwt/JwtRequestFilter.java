package com.technical.config.jwt;

import com.technical.config.jwt.service.JwtTokenServiceImpl;
import com.technical.service.jwt.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

	private final JwtUserDetailsService jwtUserDetailsService;
	private final JwtTokenUtil jwtTokenUtil;
	private final JwtTokenServiceImpl jwtTokenService;

	@Value("${bypass.urls}")
	private String byPassUrls;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
			FilterChain filterChain) throws ServletException, IOException {

		final String requestTokenHeader = httpServletRequest.getHeader("Authorization");

		String email = null;
		String jwtToken = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get
		// only the Token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				email = jwtTokenUtil.getEmailFromToken(jwtToken);

				String requestURI = httpServletRequest.getRequestURI();
				MDC.put("email", email);
				MDC.put("API", requestURI);
				log.info("{}[{}] is trying to accessing {}", email, jwtToken, requestURI);
			} catch (IllegalArgumentException e) {
				log.error("Unable to get JWT Token[{}]", jwtToken, e);
			} catch (ExpiredJwtException e) {
				jwtTokenService.invalidateJwtToken(jwtToken);
				log.error("JWT Token[{}] has expired!", jwtToken);
			}
		}

		// Once we get the token validate it.
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);
			// if token is valid configure, Spring Security to manually set authentication
			if (Boolean.TRUE.equals(jwtTokenUtil.validateToken(jwtToken, userDetails.getUsername()))) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				// After setting the Authentication in the context, we specify that the current user is authenticated.
				// So it passes the Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return byPassUrls != null && Arrays.stream(byPassUrls.split(","))
                .anyMatch(path::startsWith);
	}
}
