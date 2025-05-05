package com.gastrobar_alemans_backend.model;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "menu_item")
public class MenuItemMODEL {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 1000)
    private String description;

    private BigDecimal price;

    private String imageUrl;

    private boolean active;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private MenuCategory category;

    @OneToMany(mappedBy = "menuItem")
    private List<MenuItemPromo> promos;
    @Transient
    private BigDecimal promoPrice;

    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }
}
