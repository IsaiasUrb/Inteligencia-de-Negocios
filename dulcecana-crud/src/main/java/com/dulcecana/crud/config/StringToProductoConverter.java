package com.dulcecana.crud.config;

import com.dulcecana.crud.entity.Producto;
import com.dulcecana.crud.repository.ProductoRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToProductoConverter implements Converter<String, Producto> {

    private final ProductoRepository repository;

    public StringToProductoConverter(ProductoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Producto convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return repository.findById(Integer.valueOf(source)).orElse(null);
    }
}
