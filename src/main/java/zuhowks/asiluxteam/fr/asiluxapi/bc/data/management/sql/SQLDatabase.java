package zuhowks.asiluxteam.fr.asiluxapi.bc.data.management.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class SQLDatabase {
    private DatabaseCredantial credantial;
    private HikariDataSource hikariDataSource;

    public SQLDatabase(DatabaseCredantial databaseManager) {
        this.credantial = databaseManager;
    }

    public void initPool() {
        final HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setMaximumPoolSize(20);
        hikariConfig.setJdbcUrl(credantial.toURL());
        hikariConfig.setUsername(credantial.getUsername());
        hikariConfig.setPassword(credantial.getPassword());
        hikariConfig.setMaxLifetime(600000L);
        hikariConfig.setIdleTimeout(300000L);
        hikariConfig.setLeakDetectionThreshold(300000L);
        hikariConfig.setConnectionTimeout(10000L);

        this.hikariDataSource = new HikariDataSource(hikariConfig);
    }

    public void closePool() {
        this.hikariDataSource.close();
    }

    public Connection getConnection () {
        if (this.hikariDataSource == null) {
            System.out.println("Not connected");
            this.initPool();
        }
        try {
            return this.hikariDataSource.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
