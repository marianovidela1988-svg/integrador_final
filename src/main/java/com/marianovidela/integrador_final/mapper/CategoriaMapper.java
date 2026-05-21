package com.marianovidela.integrador_final.mapper;

import com.marianovidela.integrador_final.dto.CategoriaDTO;
import com.marianovidela.integrador_final.model.Categoria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CategoriaMapper {
    @Autowired
    private ProductoMapper productoMapper;

    public CategoriaDTO toDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());

        if (categoria.getProductos() != null) {
            dto.setProductos(categoria.getProductos().stream()
                    .map(p -> productoMapper.toDTO(p))
                    .toList());
        }
        return dto;
    }

    public Categoria toEntity(CategoriaDTO dto) {
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre());
        return categoria;
    }
}
