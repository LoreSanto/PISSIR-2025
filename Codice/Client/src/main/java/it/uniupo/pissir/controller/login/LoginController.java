package it.uniupo.pissir.controller.login;


import com.google.gson.Gson;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
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

        //Guardo prima che non ci sia già una sessione attiva
        Map<String, Object> userData = req.session().attribute("utente");
        if( userData != null) {
            // Se l'utente è già loggato, lo reindirizzo al profilo
            res.redirect("/profilo");
            return null;
        }

        Map<Object, Object> model = new HashMap<>();

        System.out.println("Richiesta alla pagina login");

        //Reset del messaggio di error
        req.session().attribute("errorMessage", "");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/login.hbs")
        );
    };

    /**
     * <h2>Gestisce la POST del form di login.</h2>
     * <p>
     *     Valida le credenziali con una richiesta HTTP verso la REST API.
     *      In caso di successo salva l'utente in sessione e reindirizza alla pagina corrispondente.
     *      In caso di errore, torna al login con messaggio d'errore.
     * </p>
     *
     */
    public static Route handleLoginPost = (Request req, Response res) -> {

        System.out.println("Richiesta accesso alla pagina profilo");

        // Ottengo email e password dal form
        String email = req.queryParams("email");
        String password = req.queryParams("password");

        // Messaggio di errore generico per credenziali errate
        String errorMessage = "Email o password errati.";

        // Utilizzo RestTemplate per chiamare il backend REST
        RestTemplate restTemplate = new RestTemplate();

        // Costruisco la URL per fare login
        String apiUrl = "http://localhost:4567/api/v1.0/login?email=" + email + "&password=" + password;


        try {
            // Effettuo la richiesta GET per verificare le credenziali
            //ResponseEntity<String> apiResponse = restTemplate.getForEntity(apiUrl, String.class);

            Map<String, Object> utente = restTemplate.getForObject(apiUrl, Map.class);

            System.out.println("Richiesta GET alla REST API: " + apiUrl);


            if (utente != null && utente.get("email") != null) {
                req.session(true).attribute("utente", utente);

                System.out.println(utente.get("tipo"));

                if (utente.get("tipo").toString().equals("admin")) {
                    System.out.println("L'utente è un amministratore");
                    req.session().attribute("admin", true);
                    res.redirect("/profiloAdmin");
                } else {
                    System.out.println("L'utente è uno user");
                    res.redirect("/profilo");
                }

                req.session().attribute("errorMessage", "");
                return null;
            } else {
                req.session().attribute("errorMessage", errorMessage);
                req.session().attribute("email", email);
                res.redirect("/login");
            }


        } catch (HttpClientErrorException e) {
            // Errore: credenziali non valide o altro errore HTTP (es. 401 Unauthorized)

            // Salvo in sessione il messaggio e il campo email per ripopolare il form
            req.session().attribute("errorMessage", errorMessage);
            req.session().attribute("email", email);

            // Redirezione alla pagina login
            res.redirect("/login");
        }

        return null;
    };

}
