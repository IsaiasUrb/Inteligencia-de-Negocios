package com.dulcecana.crud.controller.rest;

import com.dulcecana.crud.entity.DetallePedido;
import com.dulcecana.crud.repository.DetallePedidoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/detalles-pedido")
public class DetallePedidoRestController {

    private final DetallePedidoRepository repository;

    public DetallePedidoRestController(DetallePedidoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<DetallePedido> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public DetallePedido obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DetallePedido crear(@Valid @RequestBody DetallePedido detalle) {
        detalle.setIdDetalle(null);
        return repository.save(detalle);
    }

    @PutMapping("/{id}")
    public DetallePedido actualizar(@PathVariable Integer id, @Valid @RequestBody DetallePedido datos) {
        DetallePedido existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado: " + id));
        existente.setPedido(datos.getPedido());
        existente.setProducto(datos.getProducto());
        existente.setCantidad(datos.getCantidad());
        existente.setPrecioUnitario(datos.getPrecioUnitario());
        existente.setSubtotal(datos.getSubtotal());
        return repository.save(existente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalle no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
