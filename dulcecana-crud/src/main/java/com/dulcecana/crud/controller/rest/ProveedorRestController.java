package com.dulcecana.crud.controller.rest;

import com.dulcecana.crud.entity.Proveedor;
import com.dulcecana.crud.repository.ProveedorRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorRestController {

    private final ProveedorRepository repository;

    public ProveedorRestController(ProveedorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Proveedor> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Proveedor obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Proveedor crear(@Valid @RequestBody Proveedor proveedor) {
        proveedor.setIdProveedor(null);
        return repository.save(proveedor);
    }

    @PutMapping("/{id}")
    public Proveedor actualizar(@PathVariable Integer id, @Valid @RequestBody Proveedor datos) {
        Proveedor existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado: " + id));
        existente.setNombre(datos.getNombre());
        existente.setFinca(datos.getFinca());
        existente.setTelefono(datos.getTelefono());
        existente.setSector(datos.getSector());
        return repository.save(existente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Proveedor no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
