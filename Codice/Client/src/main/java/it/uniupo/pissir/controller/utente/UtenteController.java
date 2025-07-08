package it.uniupo.pissir.controller.utente;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.HashMap;
import java.util.Map;

public class UtenteController {

    /**
     * <h2>Ritorna l'email dell'utente dalla sessione.</h2>
     * <p>
     * Questa funzione estrae l'email dell'utente dalla sessione e la restituisce in formato JSON.
     * </p>
     */
    public static Route getSessionEmail = (Request req, Response res) -> {
        Map<String, Object> utente = req.session().attribute("utente");

        if (utente == null || !utente.containsKey("email")) {
            res.status(400); // Bad Request
            return "Nessun utente loggato o email non trovata nella sessione.";
        }

        String email = (String) utente.get("email");

        Map<String, String> risposta = new HashMap<>();
        risposta.put("email", email);

        res.type("application/json");
        return new Gson().toJson(risposta);
    };

    /**
     * <h2>Ritorna se ha una corsa attuale.</h2>
     *
     */
}
