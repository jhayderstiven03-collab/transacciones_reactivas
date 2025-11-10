package com.banco.transacciones_reactivas.controller;

import com.banco.transacciones_reactivas.services.TransacctionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/transacciones")
public class TransacctionController {

    private final TransacctionService service;

    public TransacctionController(TransacctionService service) {
        this.service = service;
    }

    @PostMapping("/crear-cuenta")
    public Mono<ResponseEntity<Object>> crearCuenta(@RequestParam String titular) {
        return service.crearCuenta(titular)
                .map(cuenta -> ResponseEntity.status(HttpStatus.CREATED).body((Object) cuenta))
                .onErrorResume(e -> {
                    String mensaje = e.getMessage() != null ? e.getMessage() : "Error al crear la cuenta";
                    return Mono.just(ResponseEntity.badRequest().body((Object) mensaje));
                });
    }

    @PostMapping("/transferir")
    public Mono<ResponseEntity<String>> transferir(
            @RequestParam Long origen,
            @RequestParam Long destino,
            @RequestParam Double monto) {
        return service.transferir(origen, destino, monto)
                .thenReturn(ResponseEntity.ok("Transferencia exitosa"))
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }

    @DeleteMapping("/eliminar-cuenta/{id}")
    public Mono<ResponseEntity<String>> eliminarCuenta(@PathVariable Long id) {
        return service.eliminarCuenta(id)
                .thenReturn(ResponseEntity.ok("Cuenta eliminada exitosamente"))
                .onErrorResume(e ->
                        Mono.just(ResponseEntity.badRequest().body(e.getMessage())));
    }
}


