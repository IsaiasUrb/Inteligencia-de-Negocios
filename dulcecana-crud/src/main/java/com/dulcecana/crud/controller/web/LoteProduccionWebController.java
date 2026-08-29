package com.dulcecana.crud.controller.web;

import com.dulcecana.crud.entity.LoteProduccion;
import com.dulcecana.crud.repository.LoteProduccionRepository;
import com.dulcecana.crud.repository.ProveedorRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/lotes")
public class LoteProduccionWebController {

    private final LoteProduccionRepository repository;
    private final ProveedorRepository proveedorRepository;

    public LoteProduccionWebController(LoteProduccionRepository repository, ProveedorRepository proveedorRepository) {
        this.repository = repository;
        this.proveedorRepository = proveedorRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("lotes", repository.findAll());
        return "lote/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("lote", new LoteProduccion());
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "lote/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        LoteProduccion lote = repository.findById(id).orElseThrow();
        model.addAttribute("lote", lote);
        model.addAttribute("proveedores", proveedorRepository.findAll());
        return "lote/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("lote") LoteProduccion lote, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("proveedores", proveedorRepository.findAll());
            return "lote/form";
        }
        repository.save(lote);
        return "redirect:/lotes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/lotes";
    }
}
