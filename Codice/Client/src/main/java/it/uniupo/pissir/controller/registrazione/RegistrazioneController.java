package it.uniupo.pissir.controller.registrazione;

import com.google.gson.Gson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
     * <h2>Serve la pagina per la registrazione per l'utente.</h2>
     *
     * <p>
            Questa funzione serve la paginaLogin per l'utente.
            Quando si vuole registrarsi il sistema porterà l'utente su questa pagina
     * </p>
     */
    public static Route serveRegisterPage = (Request req, Response res) -> {

        //Guardo prima che non ci sia già una sessione attiva
        Map<String, Object> userData = req.session().attribute("utente");
        if( userData != null) {
            // Se l'utente è già loggato, lo reindirizzo al profilo
            res.redirect("/profilo");
            return null;
        }

        Map<Object, Object> model = new HashMap<>();

        //Reset del messaggio di error
        req.session().attribute("errorMessage", "");

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(model, "layouts/register.hbs")
        );
    };

    //Rivedere codice
    /**
     * <h2>Gestisce la registrazione dell'utente.</h2>
     *
     * <p>
     *     Questa funzione gestisce la registrazione dell'utente.
     *     Controlla se l'utente esiste già nel database, se così non fosse registra l'utente.
     * </p>
     */
    public static Route handleRegistrazionePut = (Request req, Response res) -> {

        // Limiti per i campi (non usati al momento, puoi usarli per validazione)
        int MIN_FIELD_SIZE = 3;
        int MAX_FIELD_SIZE = 25;

        String errorMessage = "";

        Gson gson = new Gson();
        RestTemplate restTemplate = new RestTemplate();

        // Recupera i dati dal form inviato tramite POST
        String nome = req.queryParams("nome");
        String cognome = req.queryParams("cognome");
        String email = req.queryParams("email");
        String password = req.queryParams("password");

        //Validazione nome
        if (!nome.matches("[a-zA-Z]+")) {
            errorMessage = "Il nome deve contenere solo caratteri.";
        }
        if (nome.length() > MAX_FIELD_SIZE || nome.length() < MIN_FIELD_SIZE) {
            errorMessage = "Il nome deve contenere almeno tre caratteri e non superare i venticinque.";
        }

        //Validazione cognome
        if (!cognome.matches("[a-zA-Z]+")) {
            errorMessage = "Il cognome deve contenere solo caratteri.";
        }
        if (cognome.length() > MAX_FIELD_SIZE || cognome.length() < MIN_FIELD_SIZE) {
            errorMessage = "Il cognome deve contenere almeno tre caratteri e non superare i venticinque.";
        }

        // Verifica se l'utente esiste già
        try {
            ResponseEntity<String> apiResponse = restTemplate.getForEntity(
                    "http://localhost:4567/api/v1.0/register?email=" + email, String.class
            );

            if (apiResponse.getStatusCode().is2xxSuccessful()) {
                errorMessage = "L'utente esiste già nel database.";
            }
        } catch (HttpClientErrorException.NotFound e) {
            // OK: utente non trovato, si può registrare
            System.out.println("Utente non esistente, procedo con la registrazione.");
        } catch (HttpClientErrorException e) {
            // Qualsiasi altro errore
            errorMessage = "Errore durante la verifica dell'utente.";
        }

        //Validazione email
        if (!email.matches("^[a-zA-Z0-9_!#$%&'*+/=?``{|}~^.-]+@[a-zA-Z0-9.-]+$")){
            errorMessage = "E-mail non valida.";
        }

        //Validazione password   \\A(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[a-zA-Z0-9.]{8,}\\z
    if (!password.matches("\\A(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)[a-zA-Z0-9.]{8,}\\z")){
            errorMessage = "La password deve contenere:\nalmeno 8 caratteri\nalmeno una lettera maiuscola\nalmeno una lettera minuscola\nnon sono ammessi caratteri speciali.";
        }
        System.out.println(errorMessage);
        System.out.println(gson.toJson(password));
        // Se non ci sono errori, procedi con la registrazione
        if (errorMessage.isEmpty()) {
            System.out.println("Non ci sono errori");
            // Crea JSON da inviare alla REST API
            Map<String, String> body = new HashMap<>();
            body.put("nome", nome);
            body.put("cognome", cognome);
            body.put("email", email);
            body.put("password", password);

            String jsonString = gson.toJson(body);

            // Esegui la POST per la registrazione
            try {
                System.out.println("Sto eseguendo la registrazione per l'utente: " + email);
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

                ResponseEntity<String> response = restTemplate.postForEntity(
                        "http://localhost:4567/api/v1.0/register", requestEntity, String.class
                );

                if (response.getStatusCode().is2xxSuccessful()) {
                    // Registrazione riuscita, reindirizza al login
                    req.session().attribute("errorMessage", null);
                    res.redirect("/login");
                    return null;
                } else {
                    errorMessage = "Errore durante la registrazione.";
                    res.redirect("/register");
                }
            } catch (HttpClientErrorException e) {
                errorMessage = "Errore durante la registrazione: " + e.getMessage();
            }
        }

        // Se ci sono errori, prepara il messaggio per la vista
        req.session().attribute("errorMessage", errorMessage);
        req.session().attribute("nome", nome);
        req.session().attribute("cognome", cognome);
        req.session().attribute("email", email);
        req.session().attribute("password", password);

        return new HandlebarsTemplateEngine().render(
                new ModelAndView(new HashMap<>(), "layouts/register.hbs")
        );
    };

}
