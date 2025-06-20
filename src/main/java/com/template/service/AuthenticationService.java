package com.template.service;

import com.template.entity.User;
import com.template.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final UserService userService;
    
    @Value("${auth.max-login-attempts:5}")
    private int maxLoginAttempts;

    @Value("${auth.lock-duration-minutes:10}")
    private int lockDurationMinutes;
    
    public AuthenticationResult authenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
            
            // If we reach here, authentication was successful
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found after successful authentication"));
            
            userService.onLoginSuccess(user);
            
            return new AuthenticationResult(true, null, user);
            
        } catch (BadCredentialsException e) {
            // Invalid credentials
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                userService.onLoginFailure(user, maxLoginAttempts, lockDurationMinutes);
            }
            return new AuthenticationResult(false, "Invalid credentials", null);
            
        } catch (LockedException e) {
            // Account is locked
            return new AuthenticationResult(false, "Account is locked", null);
            
        } catch (DisabledException e) {
            // Account is disabled
            return new AuthenticationResult(false, "Account is disabled", null);
            
        } catch (AuthenticationException e) {
            // Other authentication exceptions
            return new AuthenticationResult(false, "Authentication failed: " + e.getMessage(), null);
        }
    }
    
    public static class AuthenticationResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public AuthenticationResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public User getUser() { return user; }
    }
} 