package com.banco.transacciones_reactivas.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("cuentas")
public class Cuenta {
    @Id
    private Long id;
    private String titular;
    private Double saldo;
    private LocalDateTime fechaCreacion;

    public Cuenta() {}

    public Cuenta(String titular, Double saldo) {
        this.titular = titular;
        this.saldo = saldo;
    }

    public Cuenta(String titular, Double saldo, LocalDateTime fechaCreacion) {
        this.titular = titular;
        this.saldo = saldo;
        this.fechaCreacion = fechaCreacion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public Double getSaldo() {
        return saldo;
    }

    public void setSaldo(Double saldo) {
        this.saldo = saldo;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}

