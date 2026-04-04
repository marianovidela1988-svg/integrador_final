package com.marianovidela.integrador_final.mapper;


import com.marianovidela.integrador_final.dto.ProductoDTO;
import com.marianovidela.integrador_final.model.Categoria;
import com.marianovidela.integrador_final.model.Producto;
import org.springframework.stereotype.Component;


@Component
public class ProductoMapper {

    public ProductoDTO toDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setCategoriaId(producto.getCategoria().getId());
        return dto;
    }

    public Producto toEntity(ProductoDTO dto, Categoria categoria) {
        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setDescripcion(dto.getDescripcion());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setCategoria(categoria);
        return producto;
    }


}
