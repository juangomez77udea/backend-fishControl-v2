package com.fiscontrolbackend.fiscontrolbackend.service;

import com.fiscontrolbackend.fiscontrolbackend.response.DatabaseStatusResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DatabaseHealthService {

    @Autowired
    @Qualifier("mainDataSource")
    private DataSource mainDataSource;

    @Autowired
    @Qualifier("userDataSource")
    private DataSource userDataSource;

    /**
     * Verifica el estado de las conexiones a ambas bases de datos
     * @return Respuesta con el estado de las conexiones
     */
    public DatabaseStatusResponse checkDatabaseConnections() {
        Map<String, DatabaseStatusResponse.DatabaseConnectionStatus> connections = new HashMap<>();
        boolean allOk = true;

        // Verificar conexión a la base de datos principal
        try (Connection conn = mainDataSource.getConnection()) {
            if (conn.isValid(5)) { // Timeout de 5 segundos
                connections.put("main", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                        .connected(true)
                        .message("Conexión exitosa a la base de datos principal")
                        .build());
            } else {
                allOk = false;
                connections.put("main", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                        .connected(false)
                        .message("La conexión a la base de datos principal no es válida")
                        .build());
            }
        } catch (SQLException e) {
            allOk = false;
            log.error("Error al conectar a la base de datos principal: {}", e.getMessage());
            connections.put("main", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                    .connected(false)
                    .message("Error al conectar a la base de datos principal")
                    .details(e.getMessage())
                    .build());
        }

        // Verificar conexión a la base de datos de usuarios
        try (Connection conn = userDataSource.getConnection()) {
            if (conn.isValid(5)) { // Timeout de 5 segundos
                connections.put("users", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                        .connected(true)
                        .message("Conexión exitosa a la base de datos de usuarios")
                        .build());
            } else {
                allOk = false;
                connections.put("users", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                        .connected(false)
                        .message("La conexión a la base de datos de usuarios no es válida")
                        .build());
            }
        } catch (SQLException e) {
            allOk = false;
            log.error("Error al conectar a la base de datos de usuarios: {}", e.getMessage());
            connections.put("users", DatabaseStatusResponse.DatabaseConnectionStatus.builder()
                    .connected(false)
                    .message("Error al conectar a la base de datos de usuarios")
                    .details(e.getMessage())
                    .build());
        }

        return DatabaseStatusResponse.builder()
                .timestamp(java.time.LocalDateTime.now())
                .allConnectionsOk(allOk)
                .connections(connections)
                .build();
    }
}
