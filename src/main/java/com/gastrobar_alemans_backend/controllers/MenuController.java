package com.gastrobar_alemans_backend.controllers;

import com.gastrobar_alemans_backend.DTO.MenuCategoryDTO;
import com.gastrobar_alemans_backend.DTO.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;

    @GetMapping
    public ResponseEntity<List<MenuCategoryDTO>> getMenu() {
        return ResponseEntity.ok(menuService.getFullMenu());
    }
}
