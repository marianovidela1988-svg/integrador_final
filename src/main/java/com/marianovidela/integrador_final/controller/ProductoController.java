package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.ProductoDTO;
import com.marianovidela.integrador_final.model.Producto;
import com.marianovidela.integrador_final.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/productos")
public class ProductoController {
    @Autowired
    private ProductoService productoService;

    // Obtener todos los Productos
    @GetMapping
    public List<ProductoDTO> obtenerTodos() {
        return productoService.obtenerTodos();
    }

    // Obtener Producto por ID
    @GetMapping("/{id}")
    public Optional<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    // Obtener Producto por nombre
    @GetMapping("/{nombre}")
    public Optional<ProductoDTO> obtenerPorNombre(@PathVariable String nombre) {
        return productoService.obtenerPorNombre(nombre);
    }

    // Crear Producto
    @PostMapping
    public Producto crear(@Valid @RequestBody Producto producto) {
        return productoService.guardar(producto);
    }

    // Actualizar Producto
    @PutMapping("/{id}")
    public Producto actualizar(@PathVariable Long id,@Valid @RequestBody Producto producto) {
        // Se setea manualmente el id del usuario por Seguridad e Integridad
        producto.setId(id);
        return productoService.guardar(producto);
    }

    // Eliminar Producto
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        productoService.eliminar(id);
    }
}
