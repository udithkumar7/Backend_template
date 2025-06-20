package com.template.controller;

import com.template.entity.Menu;
import com.template.entity.Role;
import com.template.entity.RoleMenu;
import com.template.repository.MenuRepository;
import com.template.repository.RoleMenuRepository;
import com.template.repository.RoleRepository;
import com.template.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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
    private final MenuService menuService;

    // Create a menu (superadmin only)
    @PostMapping
    public ResponseEntity<Menu> createMenu(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuService.createMenu(menu));
    }

    // List all menus (ordered by display order)
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenusOrdered();
    }

    // Get active menus only (ordered)
    @GetMapping("/active")
    public List<Menu> getActiveMenus() {
        return menuService.getActiveMenusOrdered();
    }

    // Get root menus only (no parent)
    @GetMapping("/root")
    public List<Menu> getRootMenus() {
        return menuService.getRootMenusOrdered();
    }

    // Get sub-menus for a parent menu
    @GetMapping("/{parentId}/submenu")
    public List<Menu> getSubMenus(@PathVariable Long parentId) {
        return menuService.getSubMenusOrdered(parentId);
    }

    // Get menu by ID
    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenu(@PathVariable Long id) {
        return menuService.getMenuById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update menu
    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(@PathVariable Long id, @RequestBody Menu menu) {
        try {
            return ResponseEntity.ok(menuService.updateMenu(id, menu));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete menu
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        try {
            menuService.deleteMenu(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // === Menu Ordering Endpoints ===

    // Move menu up by one position
    @PutMapping("/{id}/move-up")
    public ResponseEntity<Menu> moveMenuUp(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(menuService.moveMenuUp(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Move menu down by one position
    @PutMapping("/{id}/move-down")
    public ResponseEntity<Menu> moveMenuDown(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(menuService.moveMenuDown(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Move menu to specific position
    @PutMapping("/{id}/move-to/{position}")
    public ResponseEntity<Menu> moveMenuToPosition(@PathVariable Long id, @PathVariable Integer position) {
        try {
            return ResponseEntity.ok(menuService.moveMenuToPosition(id, position));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Bulk reorder menus
    @PutMapping("/reorder")
    public ResponseEntity<String> reorderMenus(@RequestBody List<Long> menuIds) {
        try {
            menuService.reorderMenus(menuIds);
            return ResponseEntity.ok("Menus reordered successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid menu IDs provided");
        }
    }

    // Activate/Deactivate menu
    @PutMapping("/{id}/activate")
    public ResponseEntity<String> activateMenu(@PathVariable Long id) {
        try {
            menuService.activateMenu(id);
            return ResponseEntity.ok("Menu activated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<String> deactivateMenu(@PathVariable Long id) {
        try {
            menuService.deactivateMenu(id);
            return ResponseEntity.ok("Menu deactivated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
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