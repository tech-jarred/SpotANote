package com.spotanote;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinPebble;

public class App {
    public static void main(String[] args){
        // Initialize and begin running Javalin server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Tells app to locate static files from src/main/resources/public
            config.fileRenderer(new JavalinPebble()); // Sets up the file renderer to be used

            // Define all routes to be used in application.
            config.routes.get("/", ctx -> {
                ctx.redirect("/login");
            });

            config.routes.get("/login", ctx -> {
                ctx.render("public/LoginPage.html");
            });

            config.routes.get("/home", ctx -> {
                ctx.result("Welcome to the Home Page!");
            });
        }).start(7000);

        // Print location of where server is running.
        System.out.println("Server running at http://localhost:7000/");
    }
}
