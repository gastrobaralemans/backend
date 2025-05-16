package com.gastrobar_alemans_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties("items")
    private MenuCategory category;

    @OneToMany(mappedBy = "menuItem")
    @JsonIgnore // oculta promos (opcional, sólo si genera loops)
    private List<MenuItemPromo> promos;

    @OneToMany(mappedBy = "platillo", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PlatilloIngrediente> ingredientes;

    @Transient
    private BigDecimal promoPrice;

    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }
}
