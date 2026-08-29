package com.dulcecana.crud.controller.web;

import com.dulcecana.crud.entity.Proveedor;
import com.dulcecana.crud.repository.ProveedorRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/proveedores")
public class ProveedorWebController {

    private final ProveedorRepository repository;

    public ProveedorWebController(ProveedorRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("proveedores", repository.findAll());
        return "proveedor/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("proveedor", new Proveedor());
        return "proveedor/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        Proveedor proveedor = repository.findById(id).orElseThrow();
        model.addAttribute("proveedor", proveedor);
        return "proveedor/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("proveedor") Proveedor proveedor, BindingResult result) {
        if (result.hasErrors()) {
            return "proveedor/form";
        }
        repository.save(proveedor);
        return "redirect:/proveedores";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/proveedores";
    }
}
