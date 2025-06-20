package com.template.controller;

import com.template.entity.Code;
import com.template.service.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/location-codes")
@RequiredArgsConstructor
public class CodeController {
    private final CodeService service;

    // Create or update a code (country, state, city)
    @PostMapping
    public ResponseEntity<Code> createOrUpdate(@RequestBody Code code) {
        return ResponseEntity.ok(service.createOrUpdate(code));
    }

    // Get a code by keycode
    @GetMapping("/{keycode}")
    public ResponseEntity<Code> getBykeycode(@PathVariable String keycode) {
        Optional<Code> code = service.getBykeycode(keycode);
        return code.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // Get codes by parent (e.g., get all states for a country, all cities for a state)
    @GetMapping("/parent/{parentKeycode}")
    public List<Code> getByParent(@PathVariable String parentKeycode) {
        return service.getByParentCode(parentKeycode);
    }

    // List all codes (countries, states, cities, etc.)
    @GetMapping
    public List<Code> getAll() {
        return service.getAll();
    }

    // Delete by keycode
    @DeleteMapping("/{keycode}")
    public ResponseEntity<Void> deleteBykeycode(@PathVariable String keycode) {
        service.deleteBykeycode(keycode);
        return ResponseEntity.noContent().build();
    }
}