package com.dulcecana.crud.config;

import com.dulcecana.crud.entity.LoteProduccion;
import com.dulcecana.crud.repository.LoteProduccionRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToLoteProduccionConverter implements Converter<String, LoteProduccion> {

    private final LoteProduccionRepository repository;

    public StringToLoteProduccionConverter(LoteProduccionRepository repository) {
        this.repository = repository;
    }

    @Override
    public LoteProduccion convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return repository.findById(Integer.valueOf(source)).orElse(null);
    }
}
