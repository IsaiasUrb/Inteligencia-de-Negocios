package com.dulcecana.crud.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lote_produccion")
public class LoteProduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lote")
    private Integer idLote;

    @NotNull(message = "La fecha de produccion es obligatoria")
    @Column(name = "fecha_produccion", nullable = false)
    private LocalDate fechaProduccion;

    @Column(name = "cantidad_kg")
    private BigDecimal cantidadKg;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_proveedor")
    private Proveedor proveedor;

    public Integer getIdLote() { return idLote; }
    public void setIdLote(Integer idLote) { this.idLote = idLote; }

    public LocalDate getFechaProduccion() { return fechaProduccion; }
    public void setFechaProduccion(LocalDate fechaProduccion) { this.fechaProduccion = fechaProduccion; }

    public BigDecimal getCantidadKg() { return cantidadKg; }
    public void setCantidadKg(BigDecimal cantidadKg) { this.cantidadKg = cantidadKg; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Proveedor getProveedor() { return proveedor; }
    public void setProveedor(Proveedor proveedor) { this.proveedor = proveedor; }
}
