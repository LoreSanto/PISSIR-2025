package it.uniupo.pissir.controller.corsa;

import com.google.gson.reflect.TypeToken;
import spark.ModelAndView;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.HashMap;

import java.util.Map;

import static it.uniupo.pissir.utils.Utils.convertDoublesToInts;

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
     * Questa funzione serve la pagina per visualizzare i dettagli della propria corsa attuale, accessibile a tutti gli utenti.
     * </p>
     */
    public static Route serveCorsaAttualePage = (req, res) ->{
        Map<String, Object> utente = req.session().attribute("utente");

        if (utente.get("tipo").equals("admin")) {
            res.redirect("/profiloAdmin");
            return null;
        }

        String mailUtente = (String) utente.get("email");

        String apiUrl = "http://localhost:4567/api/v1.0/corsaAttuale";

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Headers e corpo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("email", mailUtente);

            HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

            // Invio POST e ottengo risposta come JSON string
            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

            Map<String, Object> model = new HashMap<>(utente);

            if (response.getStatusCode() == HttpStatus.OK) {
                String json = response.getBody();

                //Converte JSON in mappa direttamente
                Gson gson = new Gson();
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                Map<String, Object> corsaData = gson.fromJson(json, type);

                // Converte i valori double in int
                if(corsaData!= null && !corsaData.isEmpty()){

                    corsaData = convertDoublesToInts(corsaData);
                    model.put("corsa", corsaData);
                }else{
                    model.put("nessunaCorsa", true);
                }


            } else {
                model.put("nessunaCorsa", true);
            }

            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(model, "layouts/visualizzaCorsaAttuale.hbs")
            );

        } catch (Exception e) {
            e.printStackTrace();
            req.session().attribute("errorMessage", "Errore nel recupero corsa attuale.");
            res.redirect("/profilo");
            return null;
        }
    };



}
