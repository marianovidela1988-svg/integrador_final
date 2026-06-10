package com.marianovidela.integrador_final.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> manejarErroresValidacion(MethodArgumentNotValidException ex) {

        Map<String, String> errores = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errores.put(error.getField(), error.getDefaultMessage());
        });

        return errores;
    }


    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> manejarNotFound(ResourceNotFoundException ex) {

        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return error;
    }

    // Handler global para devolver 409 Conflict con JSON limpio (Stock insuficiente)
    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> handleStock(StockInsuficienteException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("error", "STOCK_INSUFICIENTE");
        body.put("mensaje", ex.getMessage());
        body.put("producto", ex.getProducto());
        body.put("stockDisponible", ex.getStockDisponible());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

}