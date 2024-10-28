package com.example.kenect_labs_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    private String username;
    private String password;
    private String role;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;  // Modifique conforme sua lógica de negócio
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;  // Modifique conforme sua lógica de negócio
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Modifique conforme sua lógica de negócio
    }

    @Override
    public boolean isEnabled() {
        return true;  // Modifique conforme sua lógica de negócio
    }
}
