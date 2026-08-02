package com.technical.commonutil;

import lombok.Getter;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

@Getter
public class UserUtil extends UsernamePasswordAuthenticationToken {
    private final Long userId;

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     */
    public UserUtil(Object principal, Object credentials,
                    Collection<? extends GrantedAuthority> authorities, Long userId) {
        super(principal, credentials, authorities);
        this.userId = userId;
    }

    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication instanceof UserUtil) {
            return ((UserUtil) SecurityContextHolder.getContext().getAuthentication()).getUserId();
        }
        return null;
    }
}
