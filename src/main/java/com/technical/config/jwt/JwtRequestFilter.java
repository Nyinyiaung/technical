package com.technical.config.jwt;

import com.technical.commonutil.CommonUtil;
import com.technical.config.jwt.service.JwtTokenServiceImpl;
import com.technical.service.jwt.JwtUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
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
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	@Value("${bypass.urls}")
	private String byPassUrls;

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, @NonNull HttpServletResponse httpServletResponse,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

		log.info("Called JwtRequestFilter!");
		final String requestTokenHeader = httpServletRequest.getHeader("Authorization");
		final String requestURI = httpServletRequest.getRequestURI();
		
		// Device tracking information
		final String clientIP = CommonUtil.getClientIP(httpServletRequest);
		final String userAgent = httpServletRequest.getHeader("User-Agent");
		final String deviceID = CommonUtil.generateDeviceID(userAgent, clientIP);

		String email = null;
		String jwtToken = null;

		// JWT Token is in the form "Bearer token". Remove Bearer word and get
		// only the Token
		try {
			if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
				jwtToken = requestTokenHeader.substring(7);
				email = jwtTokenUtil.getEmailFromToken(jwtToken);
			}
			
			// Set MDC context for the entire request
			MDC.put("API", requestURI);
			MDC.put("email", email != null ? email : "anonymous");
			MDC.put("clientIP", clientIP);
			MDC.put("deviceID", deviceID);
			MDC.put("userAgent", userAgent != null && userAgent.length() > 100 ? userAgent.substring(0, 100) : userAgent);

			if (email != null) {
				log.info("User[{}] from device[{}] IP[{}] accessing {}", email, deviceID, clientIP, requestURI);
			} else {
				log.info("Anonymous user from device[{}] IP[{}] accessing {}", deviceID, clientIP, requestURI);
			}
			
		} catch (IllegalArgumentException e) {
			log.error("Unable to get JWT Token[{}]", jwtToken, e);
		} catch (ExpiredJwtException e) {
			jwtTokenService.invalidateJwtToken(jwtToken);
			log.error("JWT Token[{}] has expired!", jwtToken);
		}

		// Once we get the token validate it.
		if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(email);
			// if token is valid configure, Spring Security to manually set authentication
			if (jwtTokenUtil.validateToken(jwtToken, userDetails.getUsername())) {
				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
				// After setting the Authentication in the context, we specify that the current user is authenticated.
				// So it passes the Spring Security Configurations successfully.
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		try {
			filterChain.doFilter(httpServletRequest, httpServletResponse);
		} finally {
			// Clean up MDC to prevent memory leaks
			MDC.remove("email");
			MDC.remove("API");
			MDC.remove("clientIP");
			MDC.remove("deviceID");
			MDC.remove("userAgent");
			MDC.clear();
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		if (byPassUrls == null) return false;
		
		return Arrays.stream(byPassUrls.split(","))
				.anyMatch(pattern -> pathMatcher.match(pattern.trim(), path));
	}
}
