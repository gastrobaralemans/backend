package com.gastrobar_alemans_backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MenuCategoryDTO {
    private String name;
    private List<MenuItemDTO> items;
}
