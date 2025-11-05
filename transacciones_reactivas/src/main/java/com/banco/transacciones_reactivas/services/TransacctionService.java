package com.banco.transacciones_reactivas.services;

import com.banco.transacciones_reactivas.model.Cuenta;
import com.banco.transacciones_reactivas.model.Transaccion;
import com.banco.transacciones_reactivas.repository.CuentaRepository;
import com.banco.transacciones_reactivas.repository.TransaccionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Service
public class TransacctionService {

    private final CuentaRepository cuentaRepo;
    private final TransaccionRepository transaccionRepo;

    public TransacctionService(CuentaRepository cuentaRepo, TransaccionRepository transaccionRepo) {
        this.cuentaRepo = cuentaRepo;
        this.transaccionRepo = transaccionRepo;
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
}


