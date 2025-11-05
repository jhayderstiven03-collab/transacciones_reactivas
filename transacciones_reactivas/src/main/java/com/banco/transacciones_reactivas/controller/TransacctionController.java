package com.banco.transacciones_reactivas.controller;

import com.banco.transacciones_reactivas.services.TransacctionService;
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
}


