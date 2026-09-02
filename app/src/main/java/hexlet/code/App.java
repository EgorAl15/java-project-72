package hexlet.code;

import io.javalin.Javalin;

public class App {

    private static final int DEFAULT_PORT = 7070;

    public static Javalin getApp() {
        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();

            config.routes.get("/", ctx -> {
                ctx.result("Hello World");
            });
        });

        return app;
    }

    public static void main(String[] args) {
        var app = getApp();

        var port = System.getenv("PORT");

        if (port != null) {
            app.start(Integer.parseInt(port));
        } else {
            app.start(DEFAULT_PORT);
        }
    }
}