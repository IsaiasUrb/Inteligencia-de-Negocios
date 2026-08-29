package com.dulcecana.crud.controller.web;

import com.dulcecana.crud.entity.Producto;
import com.dulcecana.crud.repository.LoteProduccionRepository;
import com.dulcecana.crud.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/productos")
public class ProductoWebController {

    private final ProductoRepository repository;
    private final LoteProduccionRepository loteRepository;

    public ProductoWebController(ProductoRepository repository, LoteProduccionRepository loteRepository) {
        this.repository = repository;
        this.loteRepository = loteRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("productos", repository.findAll());
        return "producto/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("lotes", loteRepository.findAll());
        return "producto/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("producto", repository.findById(id).orElseThrow());
        model.addAttribute("lotes", loteRepository.findAll());
        return "producto/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("producto") Producto producto, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("lotes", loteRepository.findAll());
            return "producto/form";
        }
        repository.save(producto);
        return "redirect:/productos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/productos";
    }
}
