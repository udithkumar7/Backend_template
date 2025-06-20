package com.template.service.impl;

import com.template.entity.User;
import com.template.entity.Role;
import com.template.repository.UserRepository;
import com.template.repository.RoleRepository;
import com.template.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${auth.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${auth.lock-duration-minutes:10}")
    private int lockDurationMinutes;

    @Override
    public User assignRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Set<Role> roles = new HashSet<>(user.getRoles());
        for (String roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new IllegalArgumentException("Role not found: " + roleName));
            roles.add(role);
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public User removeRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Set<Role> roles = new HashSet<>(user.getRoles());
        roles.removeIf(role -> roleNames.contains(role.getName()));
        user.setRoles(roles);
        return userRepository.save(user);
    }

    @Override
    public void onLoginSuccess(User user) {
        user.resetFailedLoginAttempts();
        user.unlockAccount();
        userRepository.save(user);
    }

    @Override
    public void onLoginFailure(User user, int maxAttempts, int lockDurationMinutes) {
        user.incrementFailedLoginAttempts();
        if (user.getFailedLoginAttempts() >= maxAttempts) {
            user.lockAccount(LocalDateTime.now().plusMinutes(lockDurationMinutes));
        }
        userRepository.save(user);
    }

    @Override
    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    @Override
    public boolean updatePassword(String username, String newPassword) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean isAccountExpired(String username) {
        return userRepository.findByUsername(username)
                .map(user -> user.getAccountExpiryDate() != null && 
                            LocalDateTime.now().isAfter(user.getAccountExpiryDate()))
                .orElse(false);
    }

    @Override
    public boolean activateAccount(String username) {
        return userRepository.findByUsername(username)
                .map(user -> {
                    user.setAccountExpiryDate(LocalDateTime.now().plusYears(1)); // Extend for 1 year
                    userRepository.save(user);
                    return true;
                })
                .orElse(false);
    }
} 