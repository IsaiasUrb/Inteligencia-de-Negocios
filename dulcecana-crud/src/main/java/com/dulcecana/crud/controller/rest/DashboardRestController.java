package com.dulcecana.crud.controller.rest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/fact-ventas")
    public Map<String, Object> factVentas(@RequestParam(defaultValue = "50") int limit) {
        int safeLimit = Math.min(Math.max(limit, 1), 200);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("limit", safeLimit);
        out.put("filas", jdbcTemplate.queryForList(
                "SELECT f.id_venta_dw AS \"idVenta\", f.id_detalle_origen AS \"idDetalleOrigen\", " +
                        "f.id_pedido_origen AS \"idPedidoOrigen\", " +
                        "f.id_fecha AS \"fkFecha\", df.fecha AS fecha, " +
                        "f.id_cliente_dw AS \"fkCliente\", dc.nombre AS cliente, " +
                        "f.id_producto_dw AS \"fkProducto\", dp.nombre_producto AS producto, " +
                        "f.id_proveedor_dw AS \"fkProveedor\", dpv.nombre AS proveedor, " +
                        "f.id_lote_dw AS \"fkLote\", dl.estado AS \"loteEstado\", " +
                        "f.cantidad AS cantidad, f.precio_unitario AS \"precioUnitario\", f.subtotal AS subtotal, " +
                        "f.estado_pedido AS \"estadoPedido\", f.metodo_pago AS \"metodoPago\" " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "JOIN dw_dulce_cana.dim_cliente_dc dc ON f.id_cliente_dw = dc.id_cliente_dw " +
                        "JOIN dw_dulce_cana.dim_producto_dc dp ON f.id_producto_dw = dp.id_producto_dw " +
                        "JOIN dw_dulce_cana.dim_proveedor_dc dpv ON f.id_proveedor_dw = dpv.id_proveedor_dw " +
                        "JOIN dw_dulce_cana.dim_lote_dc dl ON f.id_lote_dw = dl.id_lote_dw " +
                        "ORDER BY df.fecha DESC, f.id_venta_dw DESC " +
                        "LIMIT ?",
                safeLimit));

        return out;
    }

    @GetMapping("/produccion")
    public Map<String, Object> produccion() {
        Map<String, Object> out = new LinkedHashMap<>();

        Map<String, Object> totales = jdbcTemplate.queryForMap(
                "SELECT SUM(fp.costo_total) AS \"costoTotal\", " +
                        "SUM(fp.costo_materia_prima) AS \"costoMateriaPrima\", " +
                        "SUM(fp.costo_mano_obra) AS \"costoManoObra\", " +
                        "SUM(fp.costo_indirecto) AS \"costoIndirecto\", " +
                        "ROUND(AVG(fp.rendimiento_pct), 2) AS \"rendimientoPromedio\" " +
                        "FROM dw_dulce_cana.fact_produccion_dc fp");
        Map<String, Object> rentabilidadTotales = jdbcTemplate.queryForMap(
                "SELECT SUM(utilidad_bruta) AS \"utilidadBruta\", SUM(ingreso_atribuido) AS \"ingresoAtribuido\" " +
                        "FROM dw_dulce_cana.vw_rentabilidad_lote");
        out.putAll(totales);
        out.putAll(rentabilidadTotales);

        out.put("costosMensuales", jdbcTemplate.queryForList(
                "SELECT anio, mes, nombre_mes AS \"nombreMes\", costo_materia_prima AS \"costoMateriaPrima\", " +
                        "costo_mano_obra AS \"costoManoObra\", costo_indirecto AS \"costoIndirecto\", " +
                        "costo_total AS \"costoTotal\" " +
                        "FROM dw_dulce_cana.vw_costos_mensuales ORDER BY anio, mes"));

        out.put("rentabilidadLote", jdbcTemplate.queryForList(
                "SELECT id_lote_origen AS \"idLote\", fecha_produccion AS \"fechaProduccion\", proveedor, " +
                        "cantidad_kg_producida AS \"cantidadKg\", costo_total AS \"costoTotal\", " +
                        "ingreso_atribuido AS \"ingresoAtribuido\", utilidad_bruta AS \"utilidadBruta\", " +
                        "margen_utilidad_pct AS \"margenUtilidadPct\", rendimiento_pct AS \"rendimientoPct\" " +
                        "FROM dw_dulce_cana.vw_rentabilidad_lote ORDER BY fecha_produccion DESC"));

        return out;
    }

    @GetMapping("/facturacion")
    public Map<String, Object> facturacion() {
        Map<String, Object> out = new LinkedHashMap<>();

        Map<String, Object> inventarioTotales = jdbcTemplate.queryForMap(
                "SELECT SUM(valor_inventario) AS \"valorInventario\", " +
                        "COUNT(*) FILTER (WHERE stock < 20) AS \"productosStockBajo\" " +
                        "FROM public.vw_inventario");
        Map<String, Object> facturaTotales = jdbcTemplate.queryForMap(
                "SELECT SUM(total_facturado) AS \"totalFacturado\", " +
                        "COUNT(*) FILTER (WHERE estado_pago = 'Pendiente') AS \"facturasPendientes\" " +
                        "FROM public.factura");
        out.putAll(inventarioTotales);
        out.putAll(facturaTotales);

        out.put("inventario", jdbcTemplate.queryForList(
                "SELECT nombre_producto AS \"nombreProducto\", tipo_producto AS \"tipoProducto\", " +
                        "stock, valor_inventario AS \"valorInventario\" " +
                        "FROM public.vw_inventario ORDER BY stock DESC"));

        out.put("facturacionMensual", jdbcTemplate.queryForList(
                "SELECT EXTRACT(YEAR FROM fecha_emision)::int AS anio, EXTRACT(MONTH FROM fecha_emision)::int AS mes, " +
                        "TO_CHAR(fecha_emision, 'TMMonth') AS \"nombreMes\", " +
                        "SUM(subtotal) AS subtotal, SUM(monto_iva) AS \"montoIva\", SUM(total_facturado) AS total " +
                        "FROM public.factura GROUP BY 1, 2, 3 ORDER BY 1, 2"));

        out.put("facturasPendientesDetalle", jdbcTemplate.queryForList(
                "SELECT f.numero_factura AS \"numeroFactura\", f.fecha_emision AS \"fechaEmision\", " +
                        "c.nombre AS cliente, f.total_facturado AS \"totalFacturado\", f.estado_pago AS \"estadoPago\" " +
                        "FROM public.factura f " +
                        "JOIN public.pedido p ON p.id_pedido = f.id_pedido " +
                        "JOIN public.cliente c ON c.id_cliente = p.id_cliente " +
                        "WHERE f.estado_pago = 'Pendiente' " +
                        "ORDER BY f.fecha_emision DESC LIMIT 10"));

        return out;
    }
}