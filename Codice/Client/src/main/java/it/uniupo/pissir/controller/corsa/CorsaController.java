package it.uniupo.pissir.controller.corsa;

import spark.ModelAndView;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.Map;

public class CorsaController {
    /**
     * <h2>Serve la pagina visualizza corse per l'admin.</h2>
     * <p>
     * Questa funzione serve la pagina per visualizzare le corse registrate, accessibile solo agli amministratori.
     * </p>
     */
    public static Route serveCorsePage = (req, res) -> {
        // Logica per servire la pagina delle corse
        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina visualizza corse per l'admin");

        if (userData.get("tipo").equals("user")) {
            res.redirect("/");
            return null;
        } else {
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/visualizzaCorseAdmin.hbs")
            );
        }
    };

    /**
     * <h2>Serve la pagina per visualizzare le corse.</h2>
     * <p>
     * Questa funzione serve la pagina per visualizzare le corse, accessibile a tutti gli utenti.
     * </p>
     */
    public static Route serveCorseUserPage = (req, res) -> {
        // Logica per servire la pagina delle corse
        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina visualizza corse");

        if (userData.get("tipo").equals("admin")) {
            res.redirect("/corseAdmin");
            return null;
        } else {
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/visualizzaCorseUser.hbs")
            );
        }
    };

    /**
     * <h2>Serve la pagina per visualizzare i dettagli della corsa attuale.</h2>
     * <p>
     * Questa funzione serve la pagina per visualizzare i dettagli di una corsa attuale, accessibile a tutti gli utenti.
     * </p>
     */
    public static Route serveCorsaAttualePage = (req, res) ->{
        // Logica per servire la pagina delle corse
        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina visualizza corse");

        if (userData.get("tipo").equals("admin")) {
            res.redirect("/profiloAdmin");
            return null;
        } else {
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/visualizzaCorsaAttuale.hbs")
            );
        }
    };
}
