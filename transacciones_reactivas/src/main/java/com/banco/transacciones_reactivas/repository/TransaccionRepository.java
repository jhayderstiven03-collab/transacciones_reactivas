package com.banco.transacciones_reactivas.repository;

import com.banco.transacciones_reactivas.model.Transaccion;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TransaccionRepository extends ReactiveCrudRepository<Transaccion, Long> {
}

