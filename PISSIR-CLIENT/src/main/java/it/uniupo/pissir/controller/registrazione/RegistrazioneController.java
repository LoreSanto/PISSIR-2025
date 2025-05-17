package it.uniupo.pissir.controller.registrazione;

import com.google.gson.Gson;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

public class RegistrazioneController {

    /**
     * Serve la pagina di registrazione
     *
     * @param req
     * @param res
     * @return la pagina di registrazione
     */
    public static Route serveRegistrazionePage = (Request req, Response res) -> {
        Map<String, Object> model = new HashMap<>();


        return new HandlebarsTemplateEngine().render(new ModelAndView(model, "registrazione.hbs"));
    };

    /**
     * Gestisce la registrazione dell'utente
     *
     * <p>
     *     Questa funzione gestisce la registrazione dell'utente.
     *     Controlla se l'utente esiste già nel database, se così non fosse registra l'utente.
     * </p>
     * @param req ci sono i dati dell'utente
     * @param res ci sono i dati della risposta
     * @return la pagina di dell'utente e registra l'utente nel database
     */
    public static Route handleRegistrazionePut = (Request req, Response res) -> {

        //Dimensioni massime e minime di nome, cognome e username
        int MIN_FIELD_SIZE = 3;
        int MAX_FIELD_SIZE = 25;

        String errorMessage = "";

        Gson gson = new Gson();
        RestTemplate restTemplate = new RestTemplate();

        //Recupero i dati dell'utente dalla richiesta
        String nome = req.queryParams("nome");
        String cognome = req.queryParams("cognome");
        String email = req.queryParams("email");
        String password = req.queryParams("password");

        try {
            ResponseEntity<String> apiResponse = restTemplate.getForEntity("http://localhost:4567/api/v1.0/utente?email=" + email, String.class);
            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                errorMessage = "L'utente esiste già nel database.";
            }
        } catch (HttpClientErrorException e) {
            System.out.println(e + "\nL'utente non esiste nel database.");
        }

        //Inserimento dati (da sviluppare in seguito con hbs)
        if(errorMessage.isEmpty()) {
            //Genero il json da mandare alla REST API
            Map<String, String> body = new HashMap<>();
            body.put("nome", nome);
            body.put("cognome", cognome);
            body.put("email", email);
            body.put("password", password);

            String jsonString = gson.toJson(body);

            //Chiamata alla REST API da applicare quando c'è già l'utente e ti porta alla pagina di login
            /*try {
                ResponseEntity<String> apiResponse = restTemplate.postForEntity("http://localhost:4567/api/v1.0/utente", jsonString, String.class);

                if(apiResponse.getStatusCode().is2xxSuccessful()){
                    //Senza questo il messaggio di errore compare negli altri form
                    errorMessage = "";
                    req.session().attribute("errorMessage", errorMessage);

                    res.redirect("/login");
                }
            } catch (HttpClientErrorException e) {
                errorMessage = "Si è verificato un problema.";
                req.session().attribute("errorMessage", errorMessage);

                res.redirect("/registrazione");
            }*/
        } else {
            //Creazione del messaggio di errore
            req.session().attribute("errorMessage", errorMessage);

            //Ricompilazione dei campi
            req.session().attribute("nome", nome);
            req.session().attribute("cognome", cognome);
            req.session().attribute("email", email);
            req.session().attribute("password", password);

            //res.redirect("/registrazione");
            System.out.println("Registrazione avvenuta con successo");
        }

        return new HandlebarsTemplateEngine().render(new ModelAndView(new HashMap<>(), "registrazione.hbs"));
    };

}
