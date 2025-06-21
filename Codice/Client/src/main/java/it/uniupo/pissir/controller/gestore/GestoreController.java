package it.uniupo.pissir.controller.gestore;

import org.springframework.web.client.RestTemplate;
import spark.ModelAndView;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class GestoreController {

    /**
     * <h2>Serve la pagina visualizza utenti per l'admin.</h2>
     * <p>
     * Questa funzione serve la pagina per visualizzare gli utenti registrati, accessibile solo agli amministratori.
     * </p>
     */
    public static Route serveUtentiPage = (req, res) -> {
        // Logica per servire la pagina degli utenti
        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina visualizza utenti per l'admin");

        if (userData.get("tipo").equals("user")) {
            res.redirect("/");
            return null;
        }else{
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/visualizzaUtenti.hbs")
            );
        }
    };


}
