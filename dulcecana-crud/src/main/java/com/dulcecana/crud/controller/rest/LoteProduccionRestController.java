package com.dulcecana.crud.controller.rest;

import com.dulcecana.crud.entity.LoteProduccion;
import com.dulcecana.crud.repository.LoteProduccionRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/lotes")
public class LoteProduccionRestController {

    private final LoteProduccionRepository repository;

    public LoteProduccionRestController(LoteProduccionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<LoteProduccion> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public LoteProduccion obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoteProduccion crear(@Valid @RequestBody LoteProduccion lote) {
        lote.setIdLote(null);
        return repository.save(lote);
    }

    @PutMapping("/{id}")
    public LoteProduccion actualizar(@PathVariable Integer id, @Valid @RequestBody LoteProduccion datos) {
        LoteProduccion existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado: " + id));
        existente.setFechaProduccion(datos.getFechaProduccion());
        existente.setCantidadKg(datos.getCantidadKg());
        existente.setEstado(datos.getEstado());
        existente.setProveedor(datos.getProveedor());
        return repository.save(existente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
