package com.technical.service.jwt;

import com.technical.dao.UserRepository;
import com.technical.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmailAndIsVerifiedTrue(email)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

		Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

		return new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(),
				user.isVerified(),
				true,
				true,
				true,
				authorities
		);
	}

}
