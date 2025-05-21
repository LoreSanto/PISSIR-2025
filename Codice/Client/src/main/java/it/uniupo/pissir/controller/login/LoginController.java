package it.uniupo.pissir.controller.login;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class LoginController {

    /**
     * <h2>Serve la pagina per la registrazione per l'utente.</h2>
     *
     * <p>
            Questa funzione serve la paginaLogin per l'utente.
            Quando si prova ad accedere/ si vuole fare il login, il sistema porterà l'utente su questa pagina
     * </p>
     */
    public static Route serveLoginPage = (Request req, Response res) -> {
        Map<Object, Object> model = new HashMap<>();

        //Reset del messaggio di error
        req.session().attribute("errorMessage", "");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/login.hbs")
        );
    };
}
