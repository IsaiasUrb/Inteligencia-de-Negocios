package com.dulcecana.crud.repository;

import com.dulcecana.crud.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Integer> {
}
