package it.uniupo.pissir.controller.profilo;


import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.template.handlebars.HandlebarsTemplateEngine;
import org.springframework.web.client.RestTemplate;



import java.util.Map;

import spark.ModelAndView;

public class ProfiloController {


    /**
     * <h2>Serve la pagina del profilo dell'utente.</h2>
     *
     * <p>
     * Questa funzione serve la pagina del profilo per l'utente.
     * Quando si prova ad accedere alla pagina del profilo, il sistema porterà l'utente su questa pagina
     * solamente se l'utente è loggato ed il tipo di utente è "user".
     * </p>
     */
    public static Route serveProfiloPage = (Request req, Response res) -> {

        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina profilo user");

        if (userData == null) {
            res.redirect("/login");
            return null;
        }

        if (!userData.get("tipo").equals("user")) {
            res.redirect("/profiloAdmin");
            return null;
        }else if(userData.get("stato").equals("sospeso")){
            res.redirect("/profiloSospeso");
            return null;
        }else{
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/profilo.hbs")
            );
        }
    };

    /**
     * <h2>Serve la pagina del profilo dell'admin.</h2>
     *
     * <p>
     * Questa funzione serve la pagina del profilo per l'admin.
     * Quando si prova ad accedere alla pagina del profilo, il sistema porterà l'admin su questa pagina
     * solamente se l'utente è loggato ed il tipo di utente è "admin".
     * </p>
     */
    public static Route serveProfiloAdminPage = (Request req, Response res) -> {

        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina profilo amministratore");

        if (userData == null) {
            res.redirect("/login");
            return null;
        }

        if (!userData.get("tipo").equals("admin")) {
            res.redirect("/profilo");
            return null;
        }else{
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/profiloAdmin.hbs")
            );
        }

    };

    /**
     * <h2>Serve la pagina del profilo sospeso dell'utente.</h2>
     *
     * <p>
     * Questa funzione serve la pagina del profilo per l'utente sospeso.
     * Quando si prova ad accedere alla pagina del profilo, il sistema porterà l'utente su questa pagina
     * solamente se l'utente è loggato ed il tipo di utente è "user" e lo stato è "sospeso".
     * </p>
     */
    public static Route serveProfiloSospesoPage = (Request req, Response res) -> {

        Map<String, Object> userData = req.session().attribute("utente");

        System.out.println("Richiesta alla pagina profilo sospeso");

        if (userData == null) {
            res.redirect("/login");
            return null;
        }

        if (!userData.get("tipo").equals("user")) {
            res.redirect("/profiloAdmin");
            return null;
        }else{
            return new HandlebarsTemplateEngine().render(
                    new ModelAndView(userData, "layouts/profiloSospeso.hbs")
            );
        }
    };
    /**
     * <h2>Gestisce la ricarica del profilo utente.</h2>
     *
     * <p>
     * Questa funzione gestisce la ricarica del profilo utente.
     * Quando l'utente invia una richiesta di ricarica, il sistema aggiornerà il credito dell'utente nella sessione.
     *
     * Al momento è gestita direttamente nel client per evitare interrogazioni al database per riavere il credito attuale.
     * Questa scelta è stata fatta per evitare di dover fare una query al database ogni volta che l'utente ricarica il suo profilo ed
     * inoltre, essendo un dato solo visualizzato, ma non gestito da qua, non ci sono problemi legati alla sicurezza.
     * </p>
     */
    public static Route handleAggiornaProfiloRicaricaPost = (Request req, Response res) -> {;

        Map<String, Object> payload = new Gson().fromJson(req.body(), Map.class);
        String email = (String) payload.get("email");
        Double importo = Double.parseDouble(payload.get("importo").toString());

        Map<String, Object> sessionData = req.session().attribute("utente");

        if (sessionData != null && sessionData.get("email").equals(email)) {
            Double creditoAttuale = Double.parseDouble(sessionData.get("credito").toString());
            sessionData.put("credito", creditoAttuale + importo);
            req.session().attribute("utente", sessionData); // aggiorna la sessione
            res.status(200);
            return "Sessione aggiornata";
        } else {
            res.status(400);
            return "Sessione non trovata o email non corrispondente";
        }
    };

    /**
     * <h2>Controlla il credito dell'utente per il pagamento di una corsa.</h2>
     *
     * <p>
     * Questa funzione controlla se l'utente ha abbastanza credito per pagare una corsa.
     * Se il credito è sufficiente, restituisce un messaggio di successo, altrimenti richiede una ricarica.
     * </p>
     */
    public static Route handleCheckCreditoUtentePost= (Request req, Response res) -> {;

        //dobbiamo ricevere il costo della corsa da pagare
        double costo = Double.parseDouble(req.queryParams("costo").toString());

        Map<String, Object> sessionData = req.session().attribute("utente");
        Double creditoAttuale = Double.parseDouble(sessionData.get("credito").toString());

        if (costo+1 <= creditoAttuale) {
            res.status(200);
            return "possibile effettuare pagamento";
        } else {
            res.status(400);
            return "richiesta ricarica";
        }
    };
}
