package com.dulcecana.crud.controller.rest;

import com.dulcecana.crud.entity.Cliente;
import com.dulcecana.crud.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteRestController {

    private final ClienteRepository repository;

    public ClienteRestController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Cliente> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Cliente obtener(@PathVariable Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado: " + id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Cliente crear(@Valid @RequestBody Cliente cliente) {
        cliente.setIdCliente(null);
        return repository.save(cliente);
    }

    @PutMapping("/{id}")
    public Cliente actualizar(@PathVariable Integer id, @Valid @RequestBody Cliente datos) {
        Cliente existente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado: " + id));
        existente.setNombre(datos.getNombre());
        existente.setTipoCliente(datos.getTipoCliente());
        existente.setTelefono(datos.getTelefono());
        existente.setCorreo(datos.getCorreo());
        existente.setCiudad(datos.getCiudad());
        existente.setFechaRegistro(datos.getFechaRegistro());
        existente.setActivo(datos.getActivo());
        return repository.save(existente);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Integer id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado: " + id);
        }
        repository.deleteById(id);
    }
}
