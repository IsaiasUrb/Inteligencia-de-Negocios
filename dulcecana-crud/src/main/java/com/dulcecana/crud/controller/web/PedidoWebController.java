package com.dulcecana.crud.controller.web;

import com.dulcecana.crud.entity.Pedido;
import com.dulcecana.crud.repository.ClienteRepository;
import com.dulcecana.crud.repository.PedidoRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/pedidos")
public class PedidoWebController {

    private final PedidoRepository repository;
    private final ClienteRepository clienteRepository;

    public PedidoWebController(PedidoRepository repository, ClienteRepository clienteRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", repository.findAll());
        return "pedido/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("pedido", new Pedido());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "pedido/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("pedido", repository.findById(id).orElseThrow());
        model.addAttribute("clientes", clienteRepository.findAll());
        return "pedido/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("pedido") Pedido pedido, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("clientes", clienteRepository.findAll());
            return "pedido/form";
        }
        repository.save(pedido);
        return "redirect:/pedidos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/pedidos";
    }
}
