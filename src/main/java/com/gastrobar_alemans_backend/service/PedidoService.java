package com.gastrobar_alemans_backend.service;

import com.gastrobar_alemans_backend.model.Ingrediente;
import com.gastrobar_alemans_backend.model.Pedido;
import com.gastrobar_alemans_backend.model.PedidoDetalle;
import com.gastrobar_alemans_backend.model.PlatilloIngrediente;
import com.gastrobar_alemans_backend.repository.IngredienteRepository;
import com.gastrobar_alemans_backend.repository.PlatilloIngredienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PlatilloIngredienteRepository recetaRepo;
    private final IngredienteRepository ingredienteRepo;

    public void descontarIngredientes(Pedido pedido) {
        for (PedidoDetalle detalle : pedido.getDetalles()) {
            var platillo = detalle.getPlatillo();
            int cantidadPlatillos = detalle.getCantidad();

            List<PlatilloIngrediente> receta = recetaRepo.findByPlatillo(platillo);

            for (PlatilloIngrediente pi : receta) {
                Ingrediente ing = pi.getIngrediente();
                int totalNecesario = pi.getCantidadRequerida() * cantidadPlatillos;

                if (ing.getCantidadDisponible() < totalNecesario) {
                    throw new RuntimeException("No hay suficiente stock para " + ing.getNombre());
                }

                ing.setCantidadDisponible(ing.getCantidadDisponible() - totalNecesario);

                if (ing.getCantidadDisponible() <= ing.getStockMinimo()) {
                    System.out.println("⚠️ Bajo stock en: " + ing.getNombre());
                }

                ingredienteRepo.save(ing);
            }
        }
    }
}
