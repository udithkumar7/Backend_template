package com.template.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.HashSet;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class User extends BaseAuditEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;
    @Column
        private String password1;
        @Column
        private String password2;
        @Column
        private String password3;
        @Column
        private String password4;
        @Column
        private String password5;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name = "ACCOUNT_NON_LOCKED")
    @Builder.Default
    private Boolean accountNonLocked = true;

    @Column(name = "ACCOUNT_LOCKED_UNTIL")
    private LocalDateTime accountLockedUntil;

    @Column(name = "ACCOUNT_EXPIRY_DATE")
    private LocalDateTime accountExpiryDate;

    @Column(name = "FAILED_LOGIN_ATTEMPTS")
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(name = "ENABLED")
    @Builder.Default
    private Boolean enabled = true;

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountExpiryDate == null || LocalDateTime.now().isBefore(accountExpiryDate);
    }

    @Override
    public boolean isAccountNonLocked() {
        if (Boolean.TRUE.equals(accountNonLocked)) {
            return true;
        }
        // Auto-unlock logic
        if (accountLockedUntil != null && LocalDateTime.now().isAfter(accountLockedUntil)) {
            unlockAccount();
            return true;
        }
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement password expiry logic if needed
    }

    @Override
    public boolean isEnabled() {
        return Boolean.TRUE.equals(enabled);
    }

    // Helper methods
    public void lockAccount(LocalDateTime lockedUntil) {
        this.accountNonLocked = false;
        this.accountLockedUntil = lockedUntil;
    }

    public void unlockAccount() {
        this.accountNonLocked = true;
        this.accountLockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts = (this.failedLoginAttempts == null ? 0 : this.failedLoginAttempts) + 1;
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
    }
}