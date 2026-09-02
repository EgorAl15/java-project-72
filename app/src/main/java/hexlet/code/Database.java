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

        if (databaseUrl == null || databaseUrl.isBlank()) {
            databaseUrl = DEFAULT_DATABASE_URL;
        }

        var config = new HikariConfig();
        config.setJdbcUrl(databaseUrl);

        return new HikariDataSource(config);
    }

    public static void init(DataSource dataSource) {
        try (
                var inputStream = Database.class
                        .getClassLoader()
                        .getResourceAsStream("schema.sql")
        ) {
            if (inputStream == null) {
                throw new RuntimeException("schema.sql not found");
            }

            var sql = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            ).lines().collect(Collectors.joining("\n"));

            try (
                    var connection = dataSource.getConnection();
                    var statement = connection.createStatement()
            ) {
                statement.execute(sql);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}