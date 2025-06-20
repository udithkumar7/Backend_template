package com.template.controller;

import com.template.entity.User;
import com.template.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/{userId}/roles")
    public ResponseEntity<User> assignRoles(
            @PathVariable Long userId,
            @RequestBody Set<String> roleNames) {
        return ResponseEntity.ok(userService.assignRoles(userId, roleNames));
    }

    @DeleteMapping("/{userId}/roles")
    public ResponseEntity<User> removeRoles(
            @PathVariable Long userId,
            @RequestBody Set<String> roleNames) {
        return ResponseEntity.ok(userService.removeRoles(userId, roleNames));
    }
} 