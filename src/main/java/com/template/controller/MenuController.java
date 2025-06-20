package com.template.controller;

import com.template.entity.Menu;
import com.template.entity.Role;
import com.template.entity.RoleMenu;
import com.template.repository.MenuRepository;
import com.template.repository.RoleMenuRepository;
import com.template.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {
    private final MenuRepository menuRepository;
    private final RoleRepository roleRepository;
    private final RoleMenuRepository roleMenuRepository;

    // Create a menu (superadmin only)
    @PostMapping
    public ResponseEntity<Menu> createMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuRepository.save(menu));
    }

    // List all menus
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    // Set menus for a role (superadmin only)
    @PostMapping("/role/{roleId}")
    public ResponseEntity<?> setMenusForRole(
            @PathVariable Long roleId,
            @RequestBody Set<Long> menuIds) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Role not found");
        }
        Role role = roleOpt.get();
        // Remove existing mappings
        List<RoleMenu> existing = roleMenuRepository.findByRole(role);
        roleMenuRepository.deleteAll(existing);
        // Add new mappings
        List<Menu> menus = menuRepository.findAllById(menuIds);
        List<RoleMenu> newMappings = menus.stream()
                .map(menu -> RoleMenu.builder().role(role).menu(menu).build())
                .collect(Collectors.toList());
        roleMenuRepository.saveAll(newMappings);
        return ResponseEntity.ok("Menus set for role");
    }

    // Get menus for a role
    @GetMapping("/role/{roleId}")
    public ResponseEntity<List<Menu>> getMenusForRole(@PathVariable Long roleId) {
        Optional<Role> roleOpt = roleRepository.findById(roleId);
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Role role = roleOpt.get();
        List<RoleMenu> roleMenus = roleMenuRepository.findByRole(role);
        List<Menu> menus = roleMenus.stream().map(RoleMenu::getMenu).collect(Collectors.toList());
        return ResponseEntity.ok(menus);
    }
} 