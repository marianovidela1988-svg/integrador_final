package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.ProductoDTO;
import com.marianovidela.integrador_final.model.Producto;
import com.marianovidela.integrador_final.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;

    // Obtener todos los Productos
    public List<ProductoDTO> obtenerTodos() {
        return productoRepository.findAll().stream().map(producto -> {
            ProductoDTO dto = new ProductoDTO();
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());
            dto.setPrecio(producto.getPrecio());
            return dto;
        }).toList();
    }

    // Obtener por Id
    public Optional<ProductoDTO> obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .map(producto -> {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setNombre(producto.getNombre());
                    dto.setDescripcion(producto.getDescripcion());
                    dto.setPrecio(producto.getPrecio());
                    return dto;
                });
    }

    // Obtener por nombre
    public Optional<ProductoDTO> obtenerPorNombre(String nombre){
        return productoRepository.findByNombre(nombre)
                .map(producto -> {
                    ProductoDTO dto = new ProductoDTO();
                    dto.setNombre(producto.getNombre());
                    dto.setDescripcion(producto.getDescripcion());
                    dto.setPrecio(producto.getPrecio());
                    return dto;
                });
    }


    // Guardar Usario (crea o actualiza uno existente)
    public Producto guardar(Producto producto) {
        return productoRepository.save(producto);
    }

    // Eliminar Usuario
    public void eliminar(Long id) {
        productoRepository.deleteById(id);
    }
}
