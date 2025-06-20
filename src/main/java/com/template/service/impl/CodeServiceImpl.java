package com.template.service.impl;

import com.template.entity.Code;
import com.template.repository.CodeRepository;
import com.template.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CodeServiceImpl implements CodeService {
    private final CodeRepository repository;

    @Override
    public Code createOrUpdate(Code code) {
        // Resolve parentCode by keycode if present
        if (code.getParentCode() != null && code.getParentCode().getKeycode() != null) {
            Optional<Code> parent = repository.findBykeycode(code.getParentCode().getKeycode());
            parent.ifPresent(code::setParentCode);
        }
        // If keycode exists, update; else create new
        Optional<Code> existing = repository.findBykeycode(code.getKeycode());
        if (existing.isPresent()) {
            Code toUpdate = existing.get();
            toUpdate.setValuekey(code.getValuekey());
            toUpdate.setCategory(code.getCategory());
            toUpdate.setParentCode(code.getParentCode());
            return repository.save(toUpdate);
        } else {
            return repository.save(code);
        }
    }

    @Override
    public Optional<Code> getBykeycode(String keycode) {
        return repository.findBykeycode(keycode);
    }

    @Override
    public List<Code> getByParentCode(String parentkeycode) {
        if (parentkeycode == null) {
            return repository.findByParentCode(null);
        }
        Optional<Code> parent = repository.findBykeycode(parentkeycode);
        return parent.map(repository::findByParentCode).orElse(List.of());
    }

    @Override
    public List<Code> getAll() {
        return repository.findAll();
    }

    @Override
    public void deleteBykeycode(String keycode) {
        repository.findBykeycode(keycode).ifPresent(repository::delete);
    }
}