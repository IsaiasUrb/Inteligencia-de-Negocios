package com.dulcecana.crud.controller.rest;

import com.dulcecana.crud.entity.Producto;
import com.dulcecana.crud.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoRestController {

    private final ProductoRepository repository;

    public ProductoRestController(ProductoRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Producto> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Producto obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Producto crear(@Valid @RequestBody Producto producto) {
        producto.setIdProducto(null);
        return repository.save(producto);
    }

    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Integer id, @Valid @RequestBody Producto datos) {
        Producto existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id));
        existente.setNombreProducto(datos.getNombreProducto());
        existente.setDescripcion(datos.getDescripcion());
        existente.setTipoProducto(datos.getTipoProducto());
        existente.setPrecio(datos.getPrecio());
        existente.setStock(datos.getStock());
        existente.setLote(datos.getLote());
        return repository.save(existente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
