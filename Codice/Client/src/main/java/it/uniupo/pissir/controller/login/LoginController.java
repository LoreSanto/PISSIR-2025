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

        System.out.println("POST /login");

        // Parso il body JSON
        Map<String, String> dati = new Gson().fromJson(req.body(), Map.class);
        String email = dati.get("email");
        String password = dati.get("password");

        // Endpoint del tuo server REST
        String apiUrl = "http://localhost:4567/api/v1.0/login";

        RestTemplate restTemplate = new RestTemplate();
        String errorMessage = "Email o password errati.";

        try {
            // Preparo il body da inviare al backend (POST JSON)
            Map<String, String> body = new HashMap<>();
            body.put("email", email);
            body.put("password", password);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            // Chiamo il backend con POST
            ResponseEntity<Map> apiResponse = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);
            Map<String, Object> utente = apiResponse.getBody();

            if (utente == null || utente.get("email") == null) {
                res.status(401);
                Map<String, String> errorMap = new HashMap<>();
                errorMap.put("error", errorMessage);
                return new Gson().toJson(errorMap);
            }

            // Creo sessione
            req.session(true).attribute("utente", utente);

            String tipo = (String) utente.get("tipo");

            Map<String, String> redirectMap = new HashMap<>();
            if ("admin".equals(tipo)) {
                req.session().attribute("admin", true);
                res.status(200);
                redirectMap.put("redirect", "/profiloAdmin");
            } else {
                res.status(200);
                redirectMap.put("redirect", "/profilo");
            }

            return new Gson().toJson(redirectMap);

        } catch (HttpClientErrorException e) {
            res.status(401);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", errorMessage);
            return new Gson().toJson(errorMap);

        } catch (Exception e) {
            e.printStackTrace();
            res.status(500);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error", "Errore del server, riprova più tardi.");
            return new Gson().toJson(errorMap);
        }

    };

}
