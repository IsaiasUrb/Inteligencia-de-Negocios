package com.dulcecana.crud.controller.rest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboards")
public class DashboardRestController {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/resumen")
    public Map<String, Object> resumen() {
        Map<String, Object> out = new LinkedHashMap<>();

        Map<String, Object> totales = jdbcTemplate.queryForMap(
                "SELECT SUM(subtotal) AS \"totalIngreso\", COUNT(DISTINCT id_pedido_origen) AS \"totalPedidos\" " +
                        "FROM dw_dulce_cana.fact_ventas_dc");
        out.put("totalIngreso", totales.get("totalIngreso"));
        out.put("totalPedidos", totales.get("totalPedidos"));

        out.put("productos", jdbcTemplate.queryForList(
                "SELECT dp.tipo_producto AS \"tipoProducto\", dp.nombre_producto AS \"nombreProducto\", " +
                        "dp.precio_lista AS \"precioLista\", SUM(f.subtotal) AS ingreso, SUM(f.cantidad) AS unidades, " +
                        "ROUND(AVG(f.precio_unitario), 2) AS \"precioPromedio\" " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_producto_dc dp ON f.id_producto_dw = dp.id_producto_dw " +
                        "GROUP BY dp.tipo_producto, dp.nombre_producto, dp.precio_lista " +
                        "ORDER BY ingreso DESC"));

        out.put("mensual", jdbcTemplate.queryForList(
                "SELECT df.anio, df.mes, df.nombre_mes AS \"nombreMes\", SUM(f.subtotal) AS ingreso, " +
                        "COUNT(DISTINCT f.id_pedido_origen) AS pedidos " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "GROUP BY df.anio, df.mes, df.nombre_mes " +
                        "ORDER BY df.anio, df.mes"));

        out.put("clientesPorTipo", jdbcTemplate.queryForList(
                "SELECT dc.tipo_cliente AS \"tipoCliente\", SUM(f.subtotal) AS ingreso, " +
                        "COUNT(DISTINCT f.id_pedido_origen) AS pedidos " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_cliente_dc dc ON f.id_cliente_dw = dc.id_cliente_dw " +
                        "GROUP BY dc.tipo_cliente ORDER BY ingreso DESC"));

        out.put("topClientes", jdbcTemplate.queryForList(
                "SELECT dc.nombre AS nombre, SUM(f.subtotal) AS ingreso " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_cliente_dc dc ON f.id_cliente_dw = dc.id_cliente_dw " +
                        "GROUP BY dc.nombre ORDER BY ingreso DESC LIMIT 5"));

        out.put("clientesActivos", jdbcTemplate.queryForMap(
                "SELECT COUNT(*) FILTER (WHERE activo) AS activos, COUNT(*) AS total " +
                        "FROM dw_dulce_cana.dim_cliente_dc"));

        out.put("estadoPedido", jdbcTemplate.queryForList(
                "SELECT estado_pedido AS estado, COUNT(DISTINCT id_pedido_origen) AS pedidos, SUM(subtotal) AS valor " +
                        "FROM dw_dulce_cana.fact_ventas_dc GROUP BY estado_pedido ORDER BY valor DESC"));

        out.put("metodoPago", jdbcTemplate.queryForList(
                "SELECT metodo_pago AS metodo, SUM(subtotal) AS valor, COUNT(DISTINCT id_pedido_origen) AS pedidos " +
                        "FROM dw_dulce_cana.fact_ventas_dc GROUP BY metodo_pago ORDER BY valor DESC"));

        out.put("proveedores", jdbcTemplate.queryForList(
                "SELECT dpv.nombre AS nombre, dpv.sector AS sector, SUM(f.subtotal) AS ingreso " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_proveedor_dc dpv ON f.id_proveedor_dw = dpv.id_proveedor_dw " +
                        "GROUP BY dpv.nombre, dpv.sector ORDER BY ingreso DESC"));

        out.put("lotes", jdbcTemplate.queryForList(
                "SELECT estado, COUNT(*) AS n, SUM(cantidad_kg) AS \"kgTotal\" " +
                        "FROM dw_dulce_cana.dim_lote_dc GROUP BY estado"));

        return out;
    }
}