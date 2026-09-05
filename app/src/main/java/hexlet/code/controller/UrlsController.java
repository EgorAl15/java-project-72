package hexlet.code.controller;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import io.javalin.http.Context;

import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UrlsController {

    public static void index(Context ctx) throws SQLException {
        var urls = UrlsRepository.getEntities();

        ctx.render("urls/index.jte", Map.of(
                "urls", urls
        ));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();

        var url = UrlsRepository.find(id);

        if (url.isEmpty()) {
            ctx.status(404);
            return;
        }

        var checks = UrlChecksRepository.findByUrlId(id);

        String flash = ctx.sessionAttribute("flash");
        ctx.req().getSession().removeAttribute("flash");

        var page = new HashMap<String, Object>();
        page.put("url", url.get());
        page.put("checks", checks);

        if (flash != null) {
            page.put("flash", flash);
        }

        ctx.render("urls/show.jte", page);
    }

    public static void create(Context ctx) throws SQLException {
        var input = ctx.formParam("url");

        String normalizedUrl;

        try {
            normalizedUrl = normalizeUrl(input);
        } catch (Exception e) {
            ctx.status(422);

            ctx.render("index.jte", Map.of(
                    "flash", "Некорректный URL"
            ));

            return;
        }

        var existingUrl = UrlsRepository.findByName(normalizedUrl);

        if (existingUrl.isPresent()) {
            ctx.sessionAttribute(
                    "flash",
                    "Страница уже существует"
            );

            ctx.redirect("/urls/" + existingUrl.get().getId());
            return;
        }

        var url = new Url(normalizedUrl);

        UrlsRepository.save(url);

        ctx.sessionAttribute(
                "flash",
                "Страница успешно добавлена"
        );

        ctx.redirect("/urls/" + url.getId());
    }

    private static String normalizeUrl(String input) throws Exception {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("URL is empty");
        }

        var url = new URI(input).toURL();

        var protocol = url.getProtocol();
        var host = url.getHost();
        var port = url.getPort();

        if (protocol == null
                || protocol.isBlank()
                || host == null
                || host.isBlank()) {
            throw new IllegalArgumentException("Invalid URL");
        }

        if (port == -1) {
            return protocol + "://" + host;
        }

        return protocol + "://" + host + ":" + port;
    }
}