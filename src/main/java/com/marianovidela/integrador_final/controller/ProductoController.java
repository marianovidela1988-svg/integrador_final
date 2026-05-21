package com.marianovidela.integrador_final.controller;

import com.marianovidela.integrador_final.dto.ProductoDTO;
import com.marianovidela.integrador_final.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "http://127.0.0.1:5500")
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
    public ProductoDTO obtenerPorId(@PathVariable Long id) {
        return productoService.obtenerPorId(id);
    }

    // Obtener Producto por nombre
    @GetMapping("/nombre/{nombre}")
    public ProductoDTO obtenerPorNombre(@PathVariable String nombre) {
        return productoService.obtenerPorNombre(nombre);
    }

   // Crear Producto
   @PostMapping
    public ProductoDTO crear(@Valid @RequestBody ProductoDTO productoDTO) {
        return productoService.crear(productoDTO);
    }

    // Actualizar Producto
    @PutMapping
    public ProductoDTO modificar(@Valid @RequestBody ProductoDTO productoDTO) {
        return productoService.modificar(productoDTO);
    }

    // Eliminar Producto
    @DeleteMapping("/{id}")
    public ProductoDTO eliminar(@PathVariable Long id) {
        return productoService.eliminar(id);
    }
}
