package com.dulcecana.crud.controller.web;

import com.dulcecana.crud.entity.DetallePedido;
import com.dulcecana.crud.repository.DetallePedidoRepository;
import com.dulcecana.crud.repository.PedidoRepository;
import com.dulcecana.crud.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/detalles")
public class DetallePedidoWebController {

    private final DetallePedidoRepository repository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;

    public DetallePedidoWebController(DetallePedidoRepository repository,
                                       PedidoRepository pedidoRepository,
                                       ProductoRepository productoRepository) {
        this.repository = repository;
        this.pedidoRepository = pedidoRepository;
        this.productoRepository = productoRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("detalles", repository.findAll());
        return "detalle/list";
    }

    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        model.addAttribute("detalle", new DetallePedido());
        model.addAttribute("pedidos", pedidoRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "detalle/form";
    }

    @GetMapping("/editar/{id}")
    public String editarForm(@PathVariable Integer id, Model model) {
        model.addAttribute("detalle", repository.findById(id).orElseThrow());
        model.addAttribute("pedidos", pedidoRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "detalle/form";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute("detalle") DetallePedido detalle, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pedidos", pedidoRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            return "detalle/form";
        }
        try {
            repository.save(detalle);
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("pedidos", pedidoRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("errorPedido",
                "No se puede guardar: el pedido seleccionado todavia no tiene un cliente asignado. " +
                "Ve a \"Pedidos\", edita ese pedido y asignale un cliente, y luego vuelve a intentar aqui.");
            return "detalle/form";
        }
        return "redirect:/detalles";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/detalles";
    }
}
