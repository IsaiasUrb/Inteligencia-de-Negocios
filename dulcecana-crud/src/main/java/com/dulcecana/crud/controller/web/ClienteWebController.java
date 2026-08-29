package com.dulcecana.crud.controller.web;

import com.dulcecana.crud.entity.Cliente;
import com.dulcecana.crud.repository.ClienteRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/clientes")
public class ClienteWebController {

    private final ClienteRepository repository;

    public ClienteWebController(ClienteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("clientes", repository.findAll());
        return "cliente/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("cliente", new Cliente());
        return "cliente/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("cliente", repository.findById(id).orElseThrow());
        return "cliente/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("cliente") Cliente cliente, BindingResult result) {
        if (result.hasErrors()) {
            return "cliente/form";
        }
        repository.save(cliente);
        return "redirect:/clientes";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/clientes";
    }
}
