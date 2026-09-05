package hexlet.code.controller;

import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlChecksRepository;
import hexlet.code.repository.UrlsRepository;
import io.javalin.http.Context;
import kong.unirest.Unirest;
import org.jsoup.Jsoup;

import java.sql.SQLException;

public class UrlChecksController {

    public static void create(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();

        var url = UrlsRepository.find(id);

        if (url.isEmpty()) {
            ctx.status(404);
            return;
        }

        try {
            var response = Unirest.get(url.get().getName())
                    .asString();

            var statusCode = response.getStatus();

            if (statusCode >= 400) {
                ctx.sessionAttribute(
                        "flash",
                        "Произошла ошибка при проверке"
                );

                ctx.redirect("/urls/" + id);
                return;
            }

            var document = Jsoup.parse(response.getBody());

            var title = document.title();

            var h1Element = document.selectFirst("h1");
            var h1 = h1Element == null
                    ? ""
                    : h1Element.text();

            var descriptionElement =
                    document.selectFirst("meta[name=description]");

            var description = descriptionElement == null
                    ? ""
                    : descriptionElement.attr("content");

            var urlCheck = new UrlCheck(
                    statusCode,
                    title,
                    h1,
                    description,
                    id
            );

            UrlChecksRepository.save(urlCheck);

            ctx.sessionAttribute(
                    "flash",
                    "Страница успешно проверена"
            );

        } catch (Exception e) {
            ctx.sessionAttribute(
                    "flash",
                    "Произошла ошибка при проверке"
            );
        }

        ctx.redirect("/urls/" + id);
    }
}