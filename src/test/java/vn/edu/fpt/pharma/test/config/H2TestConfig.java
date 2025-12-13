package vn.edu.fpt.pharma.test.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * H2 Database Test Configuration
 *
 * Registers custom functions to simulate MySQL behavior in H2 database
 * for integration testing purposes.
 *
 * This configuration is automatically loaded when running tests with H2.
 */
@TestConfiguration
public class H2TestConfig {

    /**
     * Initialize H2 database with custom MySQL-compatible functions
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource) {
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(h2DatabasePopulator());
        return initializer;
    }

    /**
     * Database populator that registers custom H2 functions
     */
    private DatabasePopulator h2DatabasePopulator() {
        return connection -> {
            try {
                // Register DATE_FORMAT function
                H2DateFormatFunction.register(connection);

                System.out.println("✅ H2 Custom Functions Registered:");
                System.out.println("   - DATE_FORMAT(timestamp, format)");

            } catch (SQLException e) {
                System.err.println("⚠️ Failed to register H2 custom functions: " + e.getMessage());
            }
        };
    }
}

