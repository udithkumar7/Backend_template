package com.template.repository;

import com.template.entity.RoleMenu;
import com.template.entity.Role;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleMenuRepository extends JpaRepository<RoleMenu, Long> {
    List<RoleMenu> findByRole(Role role);
} 