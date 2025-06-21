package it.uniupo.pissir.controller.mezzo;

import com.google.gson.Gson;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

import spark.ModelAndView;

public class MezzoController {

    /**
     * <h2>Serve la pagina visualizza mezzi user.</h2>
     * <p>
     Questa funzione serve la pagina per visualizzare i mezzi prelevabili dagli utilizzatori.
     * </p>
     */
    public static Route serveMezziPage = (req, res) -> {

        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina visualizza mezzi user");

        if (userData.get("tipo").equals("admin")) {
            res.redirect("/mezziAdmin");
            return null;
        } else {
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/visualizzaMezziUser.hbs")
            );
        }
    };

    /**
     * <h2>Serve la pagina visualizza mezzi per l'admin.</h2>
     * <p>
     * Questa funzione serve la pagina per visualizzare i mezzi registrati, accessibile solo agli amministratori.
     * </p>
     */
    public static Route serveMezziAdminPage = (req, res) -> {
        // Logica per servire la pagina dei mezzi
        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina visualizza mezzi per l'admin");

        if (userData.get("tipo").equals("user")) {
            res.redirect("/");
            return null;
        }else{
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/visualizzaMezziAdmin.hbs")
            );
        }
    };

    /**
     * <h2>Gestisce la richiesta di noleggio di un mezzo.</h2>
     * <p>
     * Questa funzione gestisce la richiesta di noleggio di un mezzo da parte dell'utente.
     * </p>
     */
    public static Route handleRichiestaNoleggioPost= (req, res) -> {

        System.out.println("Richiesta noleggio mezzo");

        // Recupero utente dalla sessione
        Map<String, Object> utente = req.session().attribute("utente");
        String mailUtente = (String) utente.get("email");

        // Recupero dati dal form
        String idMezzo = req.queryParams("id");
        String tipo = req.queryParams("tipo");
        String zona = req.queryParams("zona");
        String parcheggio = req.queryParams("parcheggio");
        String batteria = req.queryParams("batteria");

        // Creo la mappa dei dati da inviare
        Map<String, Object> datiNoleggio = new HashMap<>();
        datiNoleggio.put("email", mailUtente);
        datiNoleggio.put("id_mezzo", Integer.parseInt(idMezzo));
        datiNoleggio.put("parcheggio", parcheggio);

        // URL del backend server REST
        String apiUrl = "http://localhost:4567/api/v1.0/noleggiaMezzo";

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Imposto headers per Content-Type: application/json
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            // Creo la request con i dati e gli headers
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(datiNoleggio, headers);

            // Invio POST al server
            String response = restTemplate.postForObject(apiUrl, requestEntity, String.class);

            System.out.println("Risposta server: " + response);

            // Redirect utente dopo successo
            res.redirect("/profilo");

        } catch (Exception e) {
            e.printStackTrace();
            req.session().attribute("errorMessage", "Errore durante il noleggio del mezzo.");
            res.redirect("/mezziUser");
        }

        return null;
    };
}