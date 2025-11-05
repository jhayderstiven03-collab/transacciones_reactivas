package com.banco.transacciones_reactivas.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("cuentas")
public class Cuenta {
    @Id
    private Long id;
    private String titular;
    private Double saldo;

    public Cuenta() {}

    public Cuenta(String titular, Double saldo) {
        this.titular = titular;
        this.saldo = saldo;
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

    
}

