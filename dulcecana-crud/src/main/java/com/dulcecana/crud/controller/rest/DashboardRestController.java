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
    public Map<String, Object> resumen(@RequestParam(required = false) Integer anio) {
        Map<String, Object> out = new LinkedHashMap<>();

        Map<String, Object> totales = jdbcTemplate.queryForMap(
                "SELECT SUM(f.subtotal) AS \"totalIngreso\", COUNT(DISTINCT f.id_pedido_origen) AS \"totalPedidos\" " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ?",
                anio, anio);
        out.put("totalIngreso", totales.get("totalIngreso"));
        out.put("totalPedidos", totales.get("totalPedidos"));

        out.put("productos", jdbcTemplate.queryForList(
                "SELECT dp.tipo_producto AS \"tipoProducto\", dp.nombre_producto AS \"nombreProducto\", " +
                        "dp.precio_lista AS \"precioLista\", SUM(f.subtotal) AS ingreso, SUM(f.cantidad) AS unidades, " +
                        "ROUND(AVG(f.precio_unitario), 2) AS \"precioPromedio\" " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_producto_dc dp ON f.id_producto_dw = dp.id_producto_dw " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ? " +
                        "GROUP BY dp.tipo_producto, dp.nombre_producto, dp.precio_lista " +
                        "ORDER BY ingreso DESC",
                anio, anio));

        // OJO: esta consulta NO se filtra por año a propósito — alimenta el cálculo
        // de crecimiento (CAGR) del dashboard 01, que necesita TODO el historial
        // para comparar el primer año contra el último año completo, sin importar
        // qué año esté seleccionado en el filtro.
        out.put("productosPorAnio", jdbcTemplate.queryForList(
                "SELECT df.anio, dp.tipo_producto AS \"tipoProducto\", SUM(f.subtotal) AS ingreso " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_producto_dc dp ON f.id_producto_dw = dp.id_producto_dw " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "GROUP BY df.anio, dp.tipo_producto ORDER BY df.anio, dp.tipo_producto"));

        out.put("mensual", jdbcTemplate.queryForList(
                "SELECT df.anio, df.mes, df.nombre_mes AS \"nombreMes\", SUM(f.subtotal) AS ingreso, " +
                        "COUNT(DISTINCT f.id_pedido_origen) AS pedidos " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ? " +
                        "GROUP BY df.anio, df.mes, df.nombre_mes " +
                        "ORDER BY df.anio, df.mes",
                anio, anio));

        out.put("clientesPorTipo", jdbcTemplate.queryForList(
                "SELECT dc.tipo_cliente AS \"tipoCliente\", SUM(f.subtotal) AS ingreso, " +
                        "COUNT(DISTINCT f.id_pedido_origen) AS pedidos " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_cliente_dc dc ON f.id_cliente_dw = dc.id_cliente_dw " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ? " +
                        "GROUP BY dc.tipo_cliente ORDER BY ingreso DESC",
                anio, anio));

        out.put("topClientes", jdbcTemplate.queryForList(
                "SELECT dc.nombre AS nombre, SUM(f.subtotal) AS ingreso " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_cliente_dc dc ON f.id_cliente_dw = dc.id_cliente_dw " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ? " +
                        "GROUP BY dc.nombre ORDER BY ingreso DESC LIMIT 5",
                anio, anio));

        // Sin filtrar a propósito: es un conteo de clientes activos/totales HOY, no
        // ventas de un periodo — no tiene sentido "cuántos clientes estaban activos
        // en 2022", el estado activo es del presente.
        out.put("clientesActivos", jdbcTemplate.queryForMap(
                "SELECT COUNT(*) FILTER (WHERE activo) AS activos, COUNT(*) AS total " +
                        "FROM dw_dulce_cana.dim_cliente_dc"));

        out.put("estadoPedido", jdbcTemplate.queryForList(
                "SELECT f.estado_pedido AS estado, COUNT(DISTINCT f.id_pedido_origen) AS pedidos, SUM(f.subtotal) AS valor " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ? " +
                        "GROUP BY f.estado_pedido ORDER BY valor DESC",
                anio, anio));

        out.put("metodoPago", jdbcTemplate.queryForList(
                "SELECT f.metodo_pago AS metodo, SUM(f.subtotal) AS valor, COUNT(DISTINCT f.id_pedido_origen) AS pedidos " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ? " +
                        "GROUP BY f.metodo_pago ORDER BY valor DESC",
                anio, anio));

        out.put("proveedores", jdbcTemplate.queryForList(
                "SELECT dpv.nombre AS nombre, dpv.sector AS sector, SUM(f.subtotal) AS ingreso " +
                        "FROM dw_dulce_cana.fact_ventas_dc f " +
                        "JOIN dw_dulce_cana.dim_proveedor_dc dpv ON f.id_proveedor_dw = dpv.id_proveedor_dw " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON f.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ? " +
                        "GROUP BY dpv.nombre, dpv.sector ORDER BY ingreso DESC",
                anio, anio));

        // Sin filtrar a propósito: es el estado ACTUAL de cada lote de producción
        // (Finalizado/Proceso), no algo que dependa del año de venta filtrado.
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
    public Map<String, Object> produccion(@RequestParam(required = false) Integer anio) {
        Map<String, Object> out = new LinkedHashMap<>();

        Map<String, Object> totales = jdbcTemplate.queryForMap(
                "SELECT SUM(fp.costo_total) AS \"costoTotal\", " +
                        "SUM(fp.costo_materia_prima) AS \"costoMateriaPrima\", " +
                        "SUM(fp.costo_mano_obra) AS \"costoManoObra\", " +
                        "SUM(fp.costo_indirecto) AS \"costoIndirecto\", " +
                        "ROUND(AVG(fp.rendimiento_pct), 2) AS \"rendimientoPromedio\" " +
                        "FROM dw_dulce_cana.fact_produccion_dc fp " +
                        "JOIN dw_dulce_cana.dim_fecha_dc df ON fp.id_fecha = df.id_fecha " +
                        "WHERE CAST(? AS integer) IS NULL OR df.anio = ?",
                anio, anio);
        Map<String, Object> rentabilidadTotales = jdbcTemplate.queryForMap(
                "SELECT SUM(utilidad_bruta) AS \"utilidadBruta\", SUM(ingreso_atribuido) AS \"ingresoAtribuido\" " +
                        "FROM dw_dulce_cana.vw_rentabilidad_lote " +
                        "WHERE CAST(? AS integer) IS NULL OR EXTRACT(YEAR FROM fecha_produccion) = ?",
                anio, anio);
        out.putAll(totales);
        out.putAll(rentabilidadTotales);

        out.put("costosMensuales", jdbcTemplate.queryForList(
                "SELECT anio, mes, nombre_mes AS \"nombreMes\", costo_materia_prima AS \"costoMateriaPrima\", " +
                        "costo_mano_obra AS \"costoManoObra\", costo_indirecto AS \"costoIndirecto\", " +
                        "costo_total AS \"costoTotal\" " +
                        "FROM dw_dulce_cana.vw_costos_mensuales " +
                        "WHERE CAST(? AS integer) IS NULL OR anio = ? " +
                        "ORDER BY anio, mes",
                anio, anio));

        out.put("rentabilidadLote", jdbcTemplate.queryForList(
                "SELECT id_lote_origen AS \"idLote\", fecha_produccion AS \"fechaProduccion\", proveedor, " +
                        "cantidad_kg_producida AS \"cantidadKg\", costo_total AS \"costoTotal\", " +
                        "ingreso_atribuido AS \"ingresoAtribuido\", utilidad_bruta AS \"utilidadBruta\", " +
                        "margen_utilidad_pct AS \"margenUtilidadPct\", rendimiento_pct AS \"rendimientoPct\" " +
                        "FROM dw_dulce_cana.vw_rentabilidad_lote " +
                        "WHERE CAST(? AS integer) IS NULL OR EXTRACT(YEAR FROM fecha_produccion) = ? " +
                        "ORDER BY fecha_produccion DESC",
                anio, anio));

        return out;
    }

    @GetMapping("/facturacion")
    public Map<String, Object> facturacion(@RequestParam(required = false) Integer anio) {
        Map<String, Object> out = new LinkedHashMap<>();

        // Sin filtrar a propósito: el inventario es el stock disponible HOY, no
        // depende del año de facturación que se esté mirando.
        Map<String, Object> inventarioTotales = jdbcTemplate.queryForMap(
                "SELECT SUM(valor_inventario) AS \"valorInventario\", " +
                        "COUNT(*) FILTER (WHERE stock < 20) AS \"productosStockBajo\" " +
                        "FROM public.vw_inventario");
        Map<String, Object> facturaTotales = jdbcTemplate.queryForMap(
                "SELECT SUM(total_facturado) AS \"totalFacturado\", " +
                        "COUNT(*) FILTER (WHERE estado_pago IN ('Pendiente', 'Parcial')) AS \"facturasPendientes\" " +
                        "FROM public.factura " +
                        "WHERE CAST(? AS integer) IS NULL OR EXTRACT(YEAR FROM fecha_emision) = ?",
                anio, anio);
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
                        "FROM public.factura " +
                        "WHERE CAST(? AS integer) IS NULL OR EXTRACT(YEAR FROM fecha_emision) = ? " +
                        "GROUP BY 1, 2, 3 ORDER BY 1, 2",
                anio, anio));

        out.put("facturasPendientesDetalle", jdbcTemplate.queryForList(
                "SELECT v.numero_factura AS \"numeroFactura\", v.fecha_emision AS \"fechaEmision\", " +
                        "c.nombre AS cliente, v.total_facturado AS \"totalFacturado\", " +
                        "v.saldo_pendiente AS \"saldoPendiente\", v.estado_pago AS \"estadoPago\" " +
                        "FROM public.vw_cuentas_por_cobrar v " +
                        "JOIN public.factura f ON f.id_factura = v.id_factura " +
                        "JOIN public.pedido p ON p.id_pedido = f.id_pedido " +
                        "JOIN public.cliente c ON c.id_cliente = p.id_cliente " +
                        "WHERE v.estado_pago IN ('Pendiente', 'Parcial') " +
                        "AND (CAST(? AS integer) IS NULL OR EXTRACT(YEAR FROM v.fecha_emision) = ?) " +
                        "ORDER BY v.fecha_emision DESC LIMIT 10",
                anio, anio));

        return out;
    }

    @GetMapping("/descuentos")
    public Map<String, Object> descuentos() {
        Map<String, Object> out = new LinkedHashMap<>();

        Map<String, Object> clientesTotales = jdbcTemplate.queryForMap(
                "SELECT COUNT(*) FILTER (WHERE descuento_pct > 0) AS \"clientesConDescuento\", " +
                        "COUNT(*) AS \"totalClientes\" " +
                        "FROM public.cliente");
        out.putAll(clientesTotales);

        Map<String, Object> promedioTotales = jdbcTemplate.queryForMap(
                "SELECT ROUND(AVG(descuento_pct), 2) AS \"descuentoPromedio\" " +
                        "FROM public.cliente WHERE descuento_pct > 0");
        out.putAll(promedioTotales);

        Map<String, Object> masAlto = jdbcTemplate.queryForMap(
                "SELECT nombre, descuento_pct AS \"descuentoPct\" " +
                        "FROM public.cliente ORDER BY descuento_pct DESC LIMIT 1");
        out.putAll(masAlto);

        Map<String, Object> ingresoTotales = jdbcTemplate.queryForMap(
                "SELECT COALESCE(SUM(fv.subtotal), 0) AS \"ingresoClientesDescuento\" " +
                        "FROM dw_dulce_cana.fact_ventas_dc fv " +
                        "JOIN dw_dulce_cana.dim_cliente_dc dc ON dc.id_cliente_dw = fv.id_cliente_dw " +
                        "WHERE dc.descuento_pct > 0");
        out.putAll(ingresoTotales);

        out.put("descuentos", jdbcTemplate.queryForList(
                "SELECT nombre, tipo_cliente AS \"tipoCliente\", descuento_pct AS \"descuentoPct\" " +
                        "FROM public.vw_descuentos_cliente"));

        return out;
    }
}
