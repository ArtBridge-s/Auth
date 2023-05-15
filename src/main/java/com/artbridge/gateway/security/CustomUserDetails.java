package com.artbridge.gateway.security;

import org.springframework.security.core.GrantedAuthority;

import java.util.List;

public class CustomUserDetails extends org.springframework.security.core.userdetails.User {
    private final Long id;

    public CustomUserDetails(Long id, String login, String password, List<GrantedAuthority> grantedAuthorities) {
        super(login, password, grantedAuthorities);
        this.id = id;
    }

    public Long getId() {
        return id;
    }



    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
