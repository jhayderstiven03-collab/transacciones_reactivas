package com.banco.transacciones_reactivas.services;

import com.banco.transacciones_reactivas.model.Cuenta;
import com.banco.transacciones_reactivas.model.Transaccion;
import com.banco.transacciones_reactivas.repository.CuentaRepository;
import com.banco.transacciones_reactivas.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class TransacctionService {

    private final CuentaRepository cuentaRepo;
    private final TransaccionRepository transaccionRepo;

    public TransacctionService(CuentaRepository cuentaRepo, TransaccionRepository transaccionRepo) {
        this.cuentaRepo = cuentaRepo;
        this.transaccionRepo = transaccionRepo;
    }

    @Transactional
    public Mono<Cuenta> crearCuenta(String titular) {
        // Validar que el titular no esté vacío o nulo
        if (titular == null || titular.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("El titular no puede estar vacío"));
        }

        // Validar que el titular tenga al menos 2 caracteres
        if (titular.trim().length() < 2) {
            return Mono.error(new IllegalArgumentException("El titular debe tener al menos 2 caracteres"));
        }

        // Crear cuenta con saldo inicial en 0 y fecha de creación actual
        Cuenta nuevaCuenta = new Cuenta(titular.trim(), 0.0, LocalDateTime.now());
        return cuentaRepo.save(nuevaCuenta);
    }

    @Transactional


   



    public Mono<Void> transferir(Long idOrigen, Long idDestino, Double monto) {
        return cuentaRepo.findById(idOrigen)
                .zipWith(cuentaRepo.findById(idDestino))
                .flatMap(tuple -> {
                    Cuenta origen = tuple.getT1();
                    Cuenta destino = tuple.getT2();

                    if (origen == null || destino == null) {
                        // Registrar transacción fallida
                        Transaccion transaccionFallida = new Transaccion(idOrigen, idDestino, monto, "FALLIDA");
                        return transaccionRepo.save(transaccionFallida)
                                .then(Mono.error(new RuntimeException("Cuenta no encontrada")));
                    }

                    if (origen.getSaldo() < monto) {
                        // Registrar transacción fallida
                        Transaccion transaccionFallida = new Transaccion(idOrigen, idDestino, monto, "FALLIDA");
                        return transaccionRepo.save(transaccionFallida)
                                .then(Mono.error(new RuntimeException("Fondos insuficientes")));
                    }

                    origen.setSaldo(origen.getSaldo() - monto);
                    destino.setSaldo(destino.getSaldo() + monto);

                    return cuentaRepo.save(origen)
                            .then(cuentaRepo.save(destino))
                            .then(transaccionRepo.save(new Transaccion(idOrigen, idDestino, monto, "COMPLETADA")))
                            .then();
                });
    }

    public Mono<Void> eliminarCuenta(Long id) {
        return cuentaRepo.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta no encontrada")))
                .flatMap(cuenta -> cuentaRepo.deleteById(id));
    }
}


