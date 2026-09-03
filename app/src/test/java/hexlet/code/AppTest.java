package hexlet.code;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.net.http.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private DataSource dataSource;
    private Javalin app;

    @BeforeEach
    void setUp() {
        dataSource = Database.getDataSource(
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1"
        );

        Database.init(dataSource);

        app = App.getApp(dataSource);
    }

    @AfterEach
    void tearDown() {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            hikariDataSource.close();
        }
    }

    @Test
    void testRootPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/");

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("Анализатор страниц");
        });
    }

    @Test
    void testUrlsIndex() throws Exception {
        var url = new Url("https://example.com");
        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls");

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("https://example.com");
        });
    }

    @Test
    void testUrlShow() throws Exception {
        var url = new Url("https://example.com");
        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/" + url.getId());

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("https://example.com");
        });
    }

    @Test
    void testCreateUrl() {
        var config = new TestConfig(
                true,
                true,
                HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build()
        );

        JavalinTest.test(app, config, (server, client) -> {
            var response = client.post(
                    "/urls",
                    "url=https://example.com/some/path?key=value"
            );

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("https://example.com");

            var urls = UrlsRepository.getEntities();

            assertThat(urls).hasSize(1);
            assertThat(urls.getFirst().getName())
                    .isEqualTo("https://example.com");

            var id = urls.getFirst().getId();

            var showResponse = client.get("/urls/" + id);

            assertThat(showResponse.code()).isEqualTo(200);
            assertThat(showResponse.body().string())
                    .contains("https://example.com");
        });
    }

    @Test
    void testCreateExistingUrl() throws Exception {
        var url = new Url("https://example.com");
        UrlsRepository.save(url);

        var config = new TestConfig(
                true,
                true,
                HttpClient.newBuilder()
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build()
        );

        JavalinTest.test(app, config, (server, client) -> {
            var response = client.post(
                    "/urls",
                    "url=https://example.com/another/path"
            );

            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string())
                    .contains("https://example.com");

            var urls = UrlsRepository.getEntities();

            assertThat(urls).hasSize(1);
            assertThat(urls.getFirst().getId())
                    .isEqualTo(url.getId());

            var showResponse = client.get("/urls/" + url.getId());

            assertThat(showResponse.code()).isEqualTo(200);
            assertThat(showResponse.body().string())
                    .contains("https://example.com");
        });
    }

    @Test
    void testCreateInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(
                    "/urls",
                    "url=not-a-valid-url"
            );

            assertThat(response.code()).isEqualTo(422);
            assertThat(response.body().string())
                    .contains("Некорректный URL");

            assertThat(UrlsRepository.getEntities()).isEmpty();
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/urls/999999");

            assertThat(response.code()).isEqualTo(404);
        });
    }
}