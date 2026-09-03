package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class Database {

    private static final String DEFAULT_DATABASE_URL =
            "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";

    public static DataSource getDataSource() {
        var databaseUrl = System.getenv("JDBC_DATABASE_URL");
        var databaseUsername = System.getenv("JDBC_DATABASE_USERNAME");
        var databasePassword = System.getenv("JDBC_DATABASE_PASSWORD");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            databaseUrl = DEFAULT_DATABASE_URL;
        }

        var config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);

        if (databaseUsername != null && !databaseUsername.isBlank()) {
            config.setUsername(databaseUsername);
        }

        if (databasePassword != null && !databasePassword.isBlank()) {
            config.setPassword(databasePassword);
        }

        return new HikariDataSource(config);
    }

    public static void init(DataSource dataSource) {
        init(dataSource, "schema.sql");
    }

    public static void init(DataSource dataSource, String schemaFile) {
        try (
                var inputStream = Database.class
                        .getClassLoader()
                        .getResourceAsStream(schemaFile)
        ) {
            if (inputStream == null) {
                throw new RuntimeException(schemaFile + " not found");
            }

            var sql = new BufferedReader(
                    new InputStreamReader(
                            inputStream,
                            StandardCharsets.UTF_8
                    )
            ).lines().collect(Collectors.joining("\n"));

            try (
                    var connection = dataSource.getConnection();
                    var statement = connection.createStatement()
            ) {
                statement.execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to initialize database",
                    e
            );
        }
    }
}