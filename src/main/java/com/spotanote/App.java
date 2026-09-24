package com.spotanote;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinPebble;

public class App {
    // Initialize needed helper classes.
    private static final LoginManager authy = new LoginManager();

    public static void main(String[] args){
        // Initialize and begin running Javalin server
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public"); // Tells app to locate static files from src/main/resources/public
            config.fileRenderer(new JavalinPebble()); // Sets up the file renderer to be used

            /**
             * If sent to /, redirect user to login page. Url will always reflect what section user is in.
             * 
             * HOWEVER if there is a current user in the session, they will instead be redirected to home, to avoid having to log in again.
             */
            config.routes.get("/", ctx -> {
                if (ctx.sessionAttribute("currentUser") != null) { ctx.redirect("/home"); }
                else { ctx.redirect("/login"); }
            });

            /**
             * /login route
             * 
             * This route will be the first place any user of our system lands. When entering the system, they must provide their UNIQUE username and password.
             */
            config.routes.get("/login", ctx -> {
                if (ctx.sessionAttribute("currentUser") != null) { ctx.redirect("/home"); }
                else { ctx.render("public/LoginPage.html"); } // makes it so if user is already logged in and loads /login route, they don't have to log in again.
            });

            config.routes.post("/login", ctx -> {
                // Obtaining information entered by user on the form.
                String username = ctx.formParam("username");
                String password = ctx.formParam("password");

                // Authenticate the user.
                User user = authy.authenticate(username, password);

                if (user != null){
                    ctx.sessionAttribute("currentUser", user);
                    password = null;
                    ctx.redirect("/home");
                }
                else{
                    password = null;
                    ctx.status(401).result("Invalid username or password.");
                }
            });

            /**
             * /home route
             * 
             * This route is the central page for the user. From here they can access every part of the application, and will be presented with the most basic function for their role.
             */
            config.routes.get("/home", ctx -> {
                User currentUser = ctx.sessionAttribute("currentUser");

                // Verify user is actually logged in (prevents person from typing /home in URL to bypass login page)
                if (currentUser == null){ ctx.redirect("/login"); }

                else { ctx.result("Welcome, " + currentUser.getName() + "!");}
            });

            /**
             * /logout route
             * 
             * This route will be responsible for clearing session variables, and redirecting user to login page.
             */
            config.routes.post("/logout", ctx -> {
                ctx.req().getSession().invalidate();
                ctx.redirect("/login");
            });
        }).start(7000);

        // Print location of where server is running.
        System.out.println("Server running at http://localhost:7000/");
    }
}
