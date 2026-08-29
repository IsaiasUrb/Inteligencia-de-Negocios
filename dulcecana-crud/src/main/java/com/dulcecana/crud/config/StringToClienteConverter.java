package com.dulcecana.crud.config;

import com.dulcecana.crud.entity.Cliente;
import com.dulcecana.crud.repository.ClienteRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToClienteConverter implements Converter<String, Cliente> {

    private final ClienteRepository repository;

    public StringToClienteConverter(ClienteRepository repository) {
        this.repository = repository;
    }

    @Override
    public Cliente convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return repository.findById(Integer.valueOf(source)).orElse(null);
    }
}
