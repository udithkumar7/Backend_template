package com.template.repository;

import com.template.entity.Code;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CodeRepository extends JpaRepository<Code, Long> {
    List<Code> findByParentCode(Code parentCode);
    Optional<Code> findBykeycode(String keycode);
} 