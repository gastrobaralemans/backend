package com.gastrobar_alemans_backend.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class PlatilloIngrediente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({"ingredientes", "category", "promos"})
    private MenuItemMODEL platillo;

    @ManyToOne
    private Ingrediente ingrediente;

    private int cantidadRequerida;
}
