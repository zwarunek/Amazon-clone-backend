package com.zacharywarunek.amazonclone.account;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String first_name;
    private String last_name;
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private AccountRole role;
    private Boolean locked = false;
    private Boolean enabled = false;

    @Transient
    private String token;

    public Account(String first_name, String last_name, String username, String password, AccountRole role) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    @Override
    public String toString(){
        return String.format("Account [id=%d, first_name=%s, last_name=%s, password=%s, email=%s]", id, first_name, last_name, password, username);
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
        public boolean isEnabled() {
            return enabled;
    }
}
