package com.marianovidela.integrador_final.service;

import com.marianovidela.integrador_final.dto.ProductoDTO;
import com.marianovidela.integrador_final.exception.ResourceNotFoundException;
import com.marianovidela.integrador_final.mapper.ProductoMapper;
import com.marianovidela.integrador_final.model.Categoria;
import com.marianovidela.integrador_final.model.Producto;
import com.marianovidela.integrador_final.repository.CategoriaRepository;
import com.marianovidela.integrador_final.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ProductoMapper productoMapper;

    // Obtener todos los Productos
    public List<ProductoDTO> obtenerTodos() {
        return productoRepository.findAll()
                .stream().map(producto -> productoMapper.toDTO(producto)).toList();
    }

    // Obtener Producto por Id
    public ProductoDTO obtenerPorId(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return productoMapper.toDTO(producto);
    }

    // Obtener Producto por nombre
    public ProductoDTO obtenerPorNombre(String nombre){
        return productoRepository.findByNombre(nombre)
                .map(producto -> productoMapper.toDTO(producto))
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
    }


    // Crear Producto
    public ProductoDTO crear(ProductoDTO productoDto) {
        Categoria categoria = categoriaRepository.findById(productoDto.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        Producto producto = productoRepository.save(productoMapper.toEntity(productoDto, categoria));
        return productoMapper.toDTO(producto);
    }

    // Modificar Producto
    public ProductoDTO modificar(ProductoDTO productoDTO) {
        Producto producto = productoRepository.findById(productoDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Categoria categoria = categoriaRepository.findById(productoDTO.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());
        producto.setPrecio(productoDTO.getPrecio());
        producto.setStock(productoDTO.getStock());
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);
        return productoMapper.toDTO(actualizado);
    }

    // Eliminar Producto
    public ProductoDTO eliminar(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        productoRepository.delete(producto);
        return productoMapper.toDTO(producto);
    }
}
