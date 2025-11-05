package com.banco.transacciones_reactivas.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import com.banco.transacciones_reactivas.model.Cuenta;

public interface CuentaRepository extends ReactiveCrudRepository<Cuenta, Long> { }
