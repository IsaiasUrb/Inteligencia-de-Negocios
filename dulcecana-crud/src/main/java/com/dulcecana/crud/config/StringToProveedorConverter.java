package com.dulcecana.crud.config;

import com.dulcecana.crud.entity.Proveedor;
import com.dulcecana.crud.repository.ProveedorRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Permite que los formularios Thymeleaf (select con el id como value) se conviertan
 * automaticamente en la entidad Proveedor al enviar el formulario.
 */
@Component
public class StringToProveedorConverter implements Converter<String, Proveedor> {

    private final ProveedorRepository repository;

    public StringToProveedorConverter(ProveedorRepository repository) {
        this.repository = repository;
    }

    @Override
    public Proveedor convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return repository.findById(Integer.valueOf(source)).orElse(null);
    }
}
