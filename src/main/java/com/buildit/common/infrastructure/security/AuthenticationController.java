package com.buildit.common.infrastructure.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin
public class AuthenticationController {
    @GetMapping("/api/authenticate")
    public List<String> authenticate() {
        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication().getPrincipal();
        List<String> roles = new ArrayList<>();
        if (principal instanceof UserDetails) {
            UserDetails details = (UserDetails) principal;
            for(GrantedAuthority authority: details.getAuthorities())
                roles.add(authority.getAuthority());
        }
        return roles;
    }
}
