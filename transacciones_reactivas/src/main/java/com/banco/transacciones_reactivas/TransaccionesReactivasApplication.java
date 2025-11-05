package com.banco.transacciones_reactivas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.micrometer.common.lang.NonNull;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;

@SpringBootApplication
@EnableTransactionManagement
public class TransaccionesReactivasApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransaccionesReactivasApplication.class, args);
    }

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory cf) {
        return new R2dbcTransactionManager(cf);
    }
}
