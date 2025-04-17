package com.searchmiw.history.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSourceInitializer {

    private final DataSource dataSource;
    private final Environment env;
    
    @PostConstruct
    public void initialize() {
        try (Connection connection = dataSource.getConnection()) {
            log.info("Connected to database: {}", connection.getMetaData().getURL());
            log.info("Database product name: {}", connection.getMetaData().getDatabaseProductName());
            log.info("Database product version: {}", connection.getMetaData().getDatabaseProductVersion());
        } catch (SQLException e) {
            log.error("Error connecting to database", e);
        }
        
        // Verify JPA properties
        log.info("JPA Hibernate DDL Auto: {}", env.getProperty("spring.jpa.hibernate.ddl-auto"));
        log.info("JPA Database Platform: {}", env.getProperty("spring.jpa.database-platform"));
    }
}
