package com.dulcecana.crud.controller.web;

import com.dulcecana.crud.dto.PedidoConDetalleForm;
import com.dulcecana.crud.entity.Cliente;
import com.dulcecana.crud.entity.DetallePedido;
import com.dulcecana.crud.entity.Pedido;
import com.dulcecana.crud.entity.Producto;
import com.dulcecana.crud.repository.ClienteRepository;
import com.dulcecana.crud.repository.DetallePedidoRepository;
import com.dulcecana.crud.repository.PedidoRepository;
import com.dulcecana.crud.repository.ProductoRepository;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/pedidos")
public class PedidoWebController {

    private final PedidoRepository repository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detalleRepository;

    public PedidoWebController(PedidoRepository repository,
                                ClienteRepository clienteRepository,
                                ProductoRepository productoRepository,
                                DetallePedidoRepository detalleRepository) {
        this.repository = repository;
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.detalleRepository = detalleRepository;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("pedidos", repository.findAll());
        return "pedido/list";
    }

    // ---- Crear: formulario combinado (encabezado + productos) ----
    @GetMapping("/nuevo")
    public String nuevoForm(Model model) {
        PedidoConDetalleForm form = new PedidoConDetalleForm();
        form.getLineas().add(new PedidoConDetalleForm.Linea());
        model.addAttribute("form", form);
        model.addAttribute("clientes", clienteRepository.findAll());
        model.addAttribute("productos", productoRepository.findAll());
        return "pedido/form-nuevo";
    }

    @PostMapping("/guardar-nuevo")
    @Transactional
    public String guardarNuevo(@ModelAttribute("form") PedidoConDetalleForm form, Model model) {

        LocalDate fecha = null;
        try {
            if (form.getFechaPedido() != null && !form.getFechaPedido().isBlank()) {
                fecha = LocalDate.parse(form.getFechaPedido().trim());
            }
        } catch (Exception ignored) { }

        Integer idCliente = null;
        try {
            if (form.getIdCliente() != null && !form.getIdCliente().isBlank()) {
                idCliente = Integer.valueOf(form.getIdCliente().trim());
            }
        } catch (NumberFormatException ignored) { }

        List<Object[]> lineasValidas = new ArrayList<>();
        if (form.getLineas() != null) {
            for (PedidoConDetalleForm.Linea l : form.getLineas()) {
                if (l.getIdProducto() == null || l.getIdProducto().isBlank()) continue;
                if (l.getCantidad() == null || l.getCantidad().isBlank()) continue;
                try {
                    Integer idProducto = Integer.valueOf(l.getIdProducto().trim());
                    Integer cantidad = Integer.valueOf(l.getCantidad().trim());
                    if (cantidad <= 0) continue;
                    BigDecimal precio = null;
                    if (l.getPrecioUnitario() != null && !l.getPrecioUnitario().isBlank()) {
                        precio = new BigDecimal(l.getPrecioUnitario().trim());
                    }
                    lineasValidas.add(new Object[]{idProducto, cantidad, precio});
                } catch (NumberFormatException ignored) { }
            }
        }

        if (fecha == null || idCliente == null || lineasValidas.isEmpty()) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("errorForm",
                "Completa la fecha, el cliente, y agrega al menos un producto con cantidad valida antes de guardar.");
            if (form.getLineas() == null || form.getLineas().isEmpty()) {
                form.setLineas(new ArrayList<>(List.of(new PedidoConDetalleForm.Linea())));
            }
            model.addAttribute("form", form);
            return "pedido/form-nuevo";
        }

        try {
            Cliente cliente = clienteRepository.findById(idCliente)
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

            Pedido pedido = new Pedido();
            pedido.setFechaPedido(fecha);
            pedido.setCliente(cliente);
            pedido.setEstado("Pendiente"); // siempre se crea Pendiente; el detalle se agrega justo despues
            pedido.setMetodoPago((form.getMetodoPago() == null || form.getMetodoPago().isBlank()) ? null : form.getMetodoPago());
            pedido.setTotal(BigDecimal.ZERO);
            pedido = repository.save(pedido);

            BigDecimal total = BigDecimal.ZERO;
            for (Object[] linea : lineasValidas) {
                Integer idProducto = (Integer) linea[0];
                Integer cantidad = (Integer) linea[1];
                BigDecimal precioForm = (BigDecimal) linea[2];

                Producto producto = productoRepository.findById(idProducto)
                        .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

                BigDecimal precio = precioForm != null ? precioForm : producto.getPrecio();
                BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));

                DetallePedido detalle = new DetallePedido();
                detalle.setPedido(pedido);
                detalle.setProducto(producto);
                detalle.setCantidad(cantidad);
                detalle.setPrecioUnitario(precio);
                detalle.setSubtotal(subtotal);
                detalleRepository.save(detalle);

                total = total.add(subtotal);
            }

            String estadoFinal = (form.getEstado() == null || form.getEstado().isBlank()) ? "Pendiente" : form.getEstado();
            pedido.setTotal(total);
            pedido.setEstado(estadoFinal);
            repository.save(pedido);

        } catch (DataIntegrityViolationException | IllegalArgumentException ex) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("productos", productoRepository.findAll());
            model.addAttribute("errorForm", "No se pudo guardar el pedido: " + ex.getMessage());
            if (form.getLineas() == null || form.getLineas().isEmpty()) {
                form.setLineas(new ArrayList<>(List.of(new PedidoConDetalleForm.Linea())));
            }
            model.addAttribute("form", form);
            return "pedido/form-nuevo";
        }

        return "redirect:/pedidos";
    }

    // ---- Editar: formulario simple (solo encabezado, como antes) ----
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
        try {
            repository.save(pedido);
        } catch (DataIntegrityViolationException ex) {
            model.addAttribute("clientes", clienteRepository.findAll());
            model.addAttribute("errorEstado",
                "No se puede guardar: un pedido no puede quedar en estado \"" + pedido.getEstado() +
                "\" si todavia no tiene ningun producto en el detalle. Guardalo como \"Pendiente\", " +
                "agrega sus productos en \"Detalle de pedidos\", y luego cambia el estado.");
            return "pedido/form";
        }
        return "redirect:/pedidos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repository.deleteById(id);
        return "redirect:/pedidos";
    }
}
