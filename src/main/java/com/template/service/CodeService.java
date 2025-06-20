package com.template.service;

import com.template.entity.Code;
import java.util.List;
import java.util.Optional;

public interface CodeService {
    Code createOrUpdate(Code code);
    Optional<Code> getBykeycode(String keycode);
    List<Code> getByParentCode(String parentkeycode); // null for top-level
    List<Code> getAll();
    void deleteBykeycode(String keycode);
} 