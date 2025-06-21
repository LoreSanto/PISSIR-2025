package it.uniupo.pissir.controller.parcheggio;

import spark.ModelAndView;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.Map;

public class ParcheggioController {
    /**
     * <h2>Serve la pagina visualizza parcheggi per l'admin.</h2>
     * <p>
     * Questa funzione serve la pagina per visualizzare i parcheggi registrati, accessibile solo agli amministratori.
     * </p>
     */
    public static Route serveParcheggiPage = (req, res) -> {
        // Logica per servire la pagina dei parcheggi
        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina visualizza parcheggi per l'admin");

        if (userData.get("tipo").equals("user")) {
            res.redirect("/");
            return null;
        }else{
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/visualizzaParcheggi.hbs")
            );
        }
    };
}
