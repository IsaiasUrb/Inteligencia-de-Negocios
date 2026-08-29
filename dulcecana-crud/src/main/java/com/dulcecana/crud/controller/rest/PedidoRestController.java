package com.dulcecana.crud.controller.rest;

import com.dulcecana.crud.entity.Pedido;
import com.dulcecana.crud.repository.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoRestController {

    private final PedidoRepository repository;

    public PedidoRestController(PedidoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Pedido> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Pedido obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Pedido crear(@Valid @RequestBody Pedido pedido) {
        pedido.setIdPedido(null);
        return repository.save(pedido);
    }

    @PutMapping("/{id}")
    public Pedido actualizar(@PathVariable Integer id, @Valid @RequestBody Pedido datos) {
        Pedido existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id));
        existente.setFechaPedido(datos.getFechaPedido());
        existente.setTotal(datos.getTotal());
        existente.setEstado(datos.getEstado());
        existente.setMetodoPago(datos.getMetodoPago());
        existente.setCliente(datos.getCliente());
        return repository.save(existente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
