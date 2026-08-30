package com.dulcecana.crud.dto;

import java.util.ArrayList;
import java.util.List;

public class PedidoConDetalleForm {

    private String fechaPedido;
    private String idCliente;
    private String estado = "Pendiente";
    private String metodoPago;
    private List<Linea> lineas = new ArrayList<>();

    public String getFechaPedido() { return fechaPedido; }
    public void setFechaPedido(String fechaPedido) { this.fechaPedido = fechaPedido; }

    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }

    public List<Linea> getLineas() { return lineas; }
    public void setLineas(List<Linea> lineas) { this.lineas = lineas; }

    public static class Linea {
        private String idProducto;
        private String cantidad;
        private String precioUnitario;

        public String getIdProducto() { return idProducto; }
        public void setIdProducto(String idProducto) { this.idProducto = idProducto; }

        public String getCantidad() { return cantidad; }
        public void setCantidad(String cantidad) { this.cantidad = cantidad; }

        public String getPrecioUnitario() { return precioUnitario; }
        public void setPrecioUnitario(String precioUnitario) { this.precioUnitario = precioUnitario; }
    }
}
