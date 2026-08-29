package com.dulcecana.crud.config;

import com.dulcecana.crud.entity.*;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convierte cada entidad de vuelta a su id como String. Sin esto, Thymeleaf no puede
 * determinar cual <option> debe aparecer "seleccionado" en los formularios de edicion
 * (la conversion String -> Entidad para GUARDAR ya funciona gracias a los otros
 * conversores; estos son solo para que la vista PRE-seleccione el valor correcto).
 */
public class EntityToStringConverters {

    @Component
    public static class ProveedorToString implements Converter<Proveedor, String> {
        @Override
        public String convert(Proveedor source) {
            return source.getIdProveedor() == null ? "" : source.getIdProveedor().toString();
        }
    }

    @Component
    public static class LoteProduccionToString implements Converter<LoteProduccion, String> {
        @Override
        public String convert(LoteProduccion source) {
            return source.getIdLote() == null ? "" : source.getIdLote().toString();
        }
    }

    @Component
    public static class ClienteToString implements Converter<Cliente, String> {
        @Override
        public String convert(Cliente source) {
            return source.getIdCliente() == null ? "" : source.getIdCliente().toString();
        }
    }

    @Component
    public static class ProductoToString implements Converter<Producto, String> {
        @Override
        public String convert(Producto source) {
            return source.getIdProducto() == null ? "" : source.getIdProducto().toString();
        }
    }

    @Component
    public static class PedidoToString implements Converter<Pedido, String> {
        @Override
        public String convert(Pedido source) {
            return source.getIdPedido() == null ? "" : source.getIdPedido().toString();
        }
    }
}
