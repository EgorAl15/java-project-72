package hexlet.code.repository;

import hexlet.code.model.Url;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlsRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        var sql = """
                INSERT INTO urls (name, created_at)
                VALUES (?, ?)
                """;

        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(
                        sql,
                        Statement.RETURN_GENERATED_KEYS
                )
        ) {
            statement.setString(1, url.getName());
            statement.setTimestamp(2, url.getCreatedAt());

            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    public static Optional<Url> find(Long id) throws SQLException {
        var sql = """
                SELECT id, name, created_at
                FROM urls
                WHERE id = ?
                """;

        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql)
        ) {
            statement.setLong(1, id);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(buildUrl(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    public static Optional<Url> findByName(String name)
            throws SQLException {

        var sql = """
                SELECT id, name, created_at
                FROM urls
                WHERE name = ?
                """;

        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, name);

            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(buildUrl(resultSet));
                }

                return Optional.empty();
            }
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var urls = new ArrayList<Url>();

        var sql = """
                SELECT
                    u.id,
                    u.name,
                    u.created_at,

                    (
                        SELECT uc.status_code
                        FROM url_checks uc
                        WHERE uc.url_id = u.id
                        ORDER BY uc.created_at DESC, uc.id DESC
                        LIMIT 1
                    ) AS last_status_code,

                    (
                        SELECT uc.created_at
                        FROM url_checks uc
                        WHERE uc.url_id = u.id
                        ORDER BY uc.created_at DESC, uc.id DESC
                        LIMIT 1
                    ) AS last_check_created_at

                FROM urls u
                ORDER BY u.created_at DESC, u.id DESC
                """;

        try (
                var connection = dataSource.getConnection();
                var statement = connection.prepareStatement(sql);
                var resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                urls.add(buildUrlWithLastCheck(resultSet));
            }
        }

        return urls;
    }

    private static Url buildUrl(ResultSet resultSet)
            throws SQLException {

        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at")
        );
    }

    private static Url buildUrlWithLastCheck(ResultSet resultSet)
            throws SQLException {

        var statusCode = resultSet.getObject(
                "last_status_code",
                Integer.class
        );

        var lastCheckCreatedAt = resultSet.getTimestamp(
                "last_check_created_at"
        );

        return new Url(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getTimestamp("created_at"),
                statusCode,
                lastCheckCreatedAt
        );
    }
}