package com.fiscontrolbackend.fiscontrolbackend.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    @Autowired
    @Qualifier("mainDataSource")
    private DataSource mainDataSource;

    @Autowired
    @Qualifier("userDataSource")
    private DataSource userDataSource;

    /**
     * Endpoint para simular un error en la base de datos principal
     */
    @GetMapping("/error-main-db")
    public ResponseEntity<String> simulateMainDbError() {
        try (Connection conn = mainDataSource.getConnection()) {
            // Ejecutar una consulta inválida para forzar un error
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT * FROM tabla_que_no_existe");
            }
            return ResponseEntity.ok("Este mensaje no debería mostrarse");
        } catch (SQLException e) {
            log.error("Error simulado en la base de datos principal: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error simulado correctamente: " + e.getMessage());
        }
    }

    /**
     * Endpoint para simular un error en la base de datos de usuarios
     */
    @GetMapping("/error-user-db")
    public ResponseEntity<String> simulateUserDbError() {
        try (Connection conn = userDataSource.getConnection()) {
            // Ejecutar una consulta inválida para forzar un error
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT * FROM tabla_que_no_existe");
            }
            return ResponseEntity.ok("Este mensaje no debería mostrarse");
        } catch (SQLException e) {
            log.error("Error simulado en la base de datos de usuarios: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error simulado correctamente: " + e.getMessage());
        }
    }

    /**
     * Endpoint para simular un error de conexión cerrando la conexión
     */
    @GetMapping("/connection-error")
    public ResponseEntity<String> simulateConnectionError() {
        try {
            // Crear una conexión con timeout muy bajo
            try (Connection conn = mainDataSource.getConnection()) {
                conn.setNetworkTimeout(null, 1); // 1 ms timeout
                // Intentar ejecutar una consulta pesada
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("SELECT pg_sleep(5)"); // Dormir por 5 segundos
                }
            }
            return ResponseEntity.ok("Este mensaje no debería mostrarse");
        } catch (SQLException e) {
            log.error("Error de conexión simulado: {}", e.getMessage());
            return ResponseEntity.internalServerError().body("Error de conexión simulado correctamente: " + e.getMessage());
        }
    }
}
