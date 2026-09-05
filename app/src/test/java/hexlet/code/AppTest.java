package hexlet.code;

import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import io.javalin.testtools.TestConfig;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.http.HttpClient;

import static org.assertj.core.api.Assertions.assertThat;

class AppTest {

    private static MockWebServer mockWebServer;

    private DataSource dataSource;
    private Javalin app;

    @BeforeAll
    static void startMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void stopMockWebServer() throws IOException {
        mockWebServer.shutdown();
    }

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
            var response = client.get(
                    "/urls/" + url.getId()
            );

            assertThat(response.code()).isEqualTo(200);

            assertThat(response.body().string())
                    .contains("https://example.com")
                    .contains("Запустить проверку")
                    .contains("Проверки");
        });
    }

    @Test
    void testCreateUrl() {
        var config = new TestConfig(
                true,
                true,
                HttpClient.newBuilder()
                        .followRedirects(
                                HttpClient.Redirect.NORMAL
                        )
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

            var showResponse = client.get(
                    "/urls/" + id
            );

            assertThat(showResponse.code())
                    .isEqualTo(200);

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
                        .followRedirects(
                                HttpClient.Redirect.NORMAL
                        )
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

            var showResponse = client.get(
                    "/urls/" + url.getId()
            );

            assertThat(showResponse.code())
                    .isEqualTo(200);

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

            assertThat(
                    UrlsRepository.getEntities()
            ).isEmpty();
        });
    }

    @Test
    void testUrlNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(
                    "/urls/999999"
            );

            assertThat(response.code())
                    .isEqualTo(404);
        });
    }

    @Test
    void testCreateUrlCheck() throws Exception {
        var html = """
                <!doctype html>
                <html>
                    <head>
                        <title>Test page title</title>
                        <meta
                            name="description"
                            content="Test page description"
                        >
                    </head>
                    <body>
                        <h1>Test page h1</h1>
                    </body>
                </html>
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(
                                "Content-Type",
                                "text/html; charset=utf-8"
                        )
                        .setBody(html)
        );

        var url = new Url(
                mockWebServer.url("/").toString()
        );

        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(
                    "/urls/"
                            + url.getId()
                            + "/checks"
            );

            assertThat(response.code())
                    .isEqualTo(302);

            var checks =
                    UrlChecksRepository
                            .findByUrlId(
                                    url.getId()
                            );

            assertThat(checks)
                    .hasSize(1);

            var check = checks.getFirst();

            assertThat(check.getStatusCode())
                    .isEqualTo(200);

            assertThat(check.getTitle())
                    .isEqualTo(
                            "Test page title"
                    );

            assertThat(check.getH1())
                    .isEqualTo(
                            "Test page h1"
                    );

            assertThat(check.getDescription())
                    .isEqualTo(
                            "Test page description"
                    );

            assertThat(check.getUrlId())
                    .isEqualTo(
                            url.getId()
                    );

            assertThat(check.getCreatedAt())
                    .isNotNull();
        });
    }

    @Test
    void testUrlCheckAppearsOnShowPage()
            throws Exception {

        var html = """
                <!doctype html>
                <html>
                    <head>
                        <title>SEO title</title>
                        <meta
                            name="description"
                            content="SEO description"
                        >
                    </head>
                    <body>
                        <h1>SEO heading</h1>
                    </body>
                </html>
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(
                                "Content-Type",
                                "text/html; charset=utf-8"
                        )
                        .setBody(html)
        );

        var url = new Url(
                mockWebServer.url("/").toString()
        );

        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var checkResponse = client.post(
                    "/urls/"
                            + url.getId()
                            + "/checks"
            );

            assertThat(checkResponse.code())
                    .isEqualTo(302);

            var response = client.get(
                    "/urls/" + url.getId()
            );

            assertThat(response.code())
                    .isEqualTo(200);

            var body =
                    response.body().string();

            assertThat(body)
                    .contains("200")
                    .contains("SEO title")
                    .contains("SEO heading")
                    .contains("SEO description");
        });
    }

    @Test
    void testUrlCheckAppearsOnUrlsIndex()
            throws Exception {

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(
                                "Content-Type",
                                "text/html; charset=utf-8"
                        )
                        .setBody("""
                                <html>
                                    <body>
                                        <h1>Test</h1>
                                    </body>
                                </html>
                                """)
        );

        var url = new Url(
                mockWebServer.url("/").toString()
        );

        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var checkResponse = client.post(
                    "/urls/"
                            + url.getId()
                            + "/checks"
            );

            assertThat(checkResponse.code())
                    .isEqualTo(302);

            var response =
                    client.get("/urls");

            assertThat(response.code())
                    .isEqualTo(200);

            var body =
                    response.body().string();

            assertThat(body)
                    .contains(url.getName())
                    .contains("200");

            var urls =
                    UrlsRepository.getEntities();

            assertThat(urls)
                    .hasSize(1);

            assertThat(
                    urls.getFirst()
                            .getLastCheckStatusCode()
            ).isEqualTo(200);

            assertThat(
                    urls.getFirst()
                            .getLastCheckCreatedAt()
            ).isNotNull();
        });
    }

    @Test
    void testUrlCheckError() throws Exception {
        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(500)
                        .setBody("Server error")
        );

        var url = new Url(
                mockWebServer.url("/").toString()
        );

        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.post(
                    "/urls/"
                            + url.getId()
                            + "/checks"
            );

            assertThat(response.code())
                    .isEqualTo(302);

            var checks =
                    UrlChecksRepository
                            .findByUrlId(
                                    url.getId()
                            );

            assertThat(checks)
                    .isEmpty();
        });
    }

    @Test
    void testUrlCheckNotFound() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post(
                    "/urls/999999/checks"
            );

            assertThat(response.code())
                    .isEqualTo(404);
        });
    }

    @Test
    void testLongSeoFieldsAreTruncated()
            throws Exception {

        var longText =
                "a".repeat(250);

        var html = """
                <!doctype html>
                <html>
                    <head>
                        <title>%s</title>
                        <meta
                            name="description"
                            content="%s"
                        >
                    </head>
                    <body>
                        <h1>%s</h1>
                    </body>
                </html>
                """.formatted(
                longText,
                longText,
                longText
        );

        mockWebServer.enqueue(
                new MockResponse()
                        .setResponseCode(200)
                        .setHeader(
                                "Content-Type",
                                "text/html; charset=utf-8"
                        )
                        .setBody(html)
        );

        var url = new Url(
                mockWebServer.url("/").toString()
        );

        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var checkResponse = client.post(
                    "/urls/"
                            + url.getId()
                            + "/checks"
            );

            assertThat(checkResponse.code())
                    .isEqualTo(302);

            var checks =
                    UrlChecksRepository
                            .findByUrlId(
                                    url.getId()
                            );

            assertThat(checks)
                    .hasSize(1);

            /*
             * В базе должен оставаться
             * исходный полный текст.
             */
            assertThat(
                    checks.getFirst().getTitle()
            ).hasSize(250);

            assertThat(
                    checks.getFirst().getH1()
            ).hasSize(250);

            assertThat(
                    checks.getFirst()
                            .getDescription()
            ).hasSize(250);

            var response = client.get(
                    "/urls/" + url.getId()
            );

            assertThat(response.code())
                    .isEqualTo(200);

            var body =
                    response.body().string();

            var expected =
                    "a".repeat(200) + "...";

            assertThat(body)
                    .contains(expected);

            assertThat(body)
                    .doesNotContain(
                            "a".repeat(201)
                    );
        });
    }
}