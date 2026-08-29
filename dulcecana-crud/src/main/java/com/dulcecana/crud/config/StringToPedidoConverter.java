package com.dulcecana.crud.config;

import com.dulcecana.crud.entity.Pedido;
import com.dulcecana.crud.repository.PedidoRepository;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPedidoConverter implements Converter<String, Pedido> {

    private final PedidoRepository repository;

    public StringToPedidoConverter(PedidoRepository repository) {
        this.repository = repository;
    }

    @Override
    public Pedido convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        return repository.findById(Integer.valueOf(source)).orElse(null);
    }
}
