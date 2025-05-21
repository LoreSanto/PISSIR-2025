package it.uniupo.pissir.controller.index;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class IndexController {

    /**
     * <h2>Serve la pagina Home del client.</h2>
     * <p>
            Questa funzione serve la pagina Home del client.
            Appena si avvia il client, viene chiamata questa funzione che fa visualizzare la home page.
     * </p>
     */
    public static Route serveIndexPage = (Request req, Response res) -> {
        Map<Object, Object> model = new HashMap<>();

        //Reset del messaggio di error
        req.session().attribute("errorMessage", "");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/index.hbs")
        );
    };

}
