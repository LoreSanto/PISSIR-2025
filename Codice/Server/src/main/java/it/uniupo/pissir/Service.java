package it.uniupo.pissir;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;
import com.google.gson.Gson;
import it.uniupo.pissir.oggetti.corsa.Corsa;
import it.uniupo.pissir.oggetti.corsa.CorsaDao;
import it.uniupo.pissir.oggetti.mezzo.*;
import it.uniupo.pissir.oggetti.parcheggio.Parcheggio;
import it.uniupo.pissir.oggetti.parcheggio.ParcheggioDao;
import it.uniupo.pissir.oggetti.user.*;

//import it.uniupo.pissir.oggetti.utente.Utilizzatore;
//import it.uniupo.pissir.oggetti.utente.UtenteDao;

public class Service {

    public static void main(String[] args) {
        Gson gson = new Gson();
        String baseURL = "/api/v1.0";

        enableCORS();
        //Login di un utente !!!Poi modificare perchè non dovrebbe salvare la passwd in sessione sul client!!!
        get( baseURL + "/login", (req, res) -> {

            System.out.println("GET Login");

            String email = req.queryParams("email");
            String password = req.queryParams("password");

            String tipo = UtenteDao.getTipoUtente(email, password);

            System.out.println(tipo);

             assert tipo != null;
             if(tipo.equals("user")) {
                System.out.println("GET USER");
                Utente utente = UtenteDao.getUtenteBase(email,password,tipo);
                res.status(200);
                res.type("application/json");
                return new Gson().toJson(utente);
            }else if(tipo.equals("admin")) {
                System.out.println("GET ADMIN");
                res.status(200);
                Gestore gestore = UtenteDao.getUtenteAdmin(email,password,tipo);
                res.type("application/json");
                return new Gson().toJson(gestore);
            }else{
                System.out.println("Login fallito");
                res.status(401); // Unauthorized
                return "Login fallito";
            }

        });


        //Registrazione di un nuovo utente
        get(baseURL + "/register", (req, res) -> {
            System.out.println("GET Register");
            String email = req.queryParams("email");
            if (UtenteDao.existsByEmail(email)) {
                res.status(200);
                return "Utente trovato";
            } else {
                res.status(404);
                return "Utente non trovato";
            }
        });

        //Registrazione di un nuovo utente
        post(baseURL + "/register", (req, res) -> {
            System.out.println("POST USER");

            Map<String, String> body = gson.fromJson(req.body(), Map.class);

            String nome = body.get("nome");
            String cognome = body.get("cognome");
            String email = body.get("email");
            String password = body.get("password");

            if (UtenteDao.existsByEmail(email)) {
                res.status(409); // Conflict
                return "Email già registrata.";
            }

            Utente utente = UtenteDao.addUtente(new Utente(nome, cognome, email, password));

            res.status(201); // Created
            return "Utente registrato con successo.";
        });

        //Visualizza tutti gli utenti
        get( baseURL + "/users", (req, res) -> {
            System.out.println("GET USERS");

            ArrayList<Utente> utenti = UtenteDao.getAllUtenti();

            if (utenti.isEmpty()) {
                res.status(404); // Not Found
                return "Nessun utente trovato.";
            }

            res.status(200); // OK
            res.type("application/json");
            return new Gson().toJson(utenti);
        });

        //Aggiorna lo stato di un utente
        post(baseURL + "/updateUserStatus", (request, response) -> {
            response.type("application/json");

            try {
                // Deserializza il JSON in arrivo direttamente in un oggetto Utente.
                // Gson popolerà i campi 'email' e 'stato' dal JSON.
                // Altri campi della classe Utente che non sono nel JSON
                // rimarranno null o ai loro valori di default.
                Utente datiAggiornamento = gson.fromJson(request.body(), Utente.class);

                if (datiAggiornamento == null || datiAggiornamento.getEmail() == null || datiAggiornamento.getStato() == null) {
                    response.status(400); // Bad Request
                    return gson.toJson(new ErrorResponse("Payload mancante o malformato. Richiesti 'email' e 'stato'."));
                }

                String email = datiAggiornamento.getEmail();
                String nuovoStato = datiAggiornamento.getStato();
                int numSospensioni = datiAggiornamento.getNumSospensioni();

                boolean successo = UtenteDao.aggiornaStatoUtente(email, nuovoStato, numSospensioni);

                if (successo) {
                    //prima aumento il numero di sospesioni nel caso in cui il nuovo stato sia "sospeso"
                    if(nuovoStato.equals("sospeso"))UtenteDao.upNumeroSospensioni(email);
                    response.status(200); // OK
                    return gson.toJson(new SuccessResponse("Stato utente aggiornato con successo per: " + email));
                } else {
                    // Se l'aggiornamento fallisce (es. utente non trovato se il DAO lo segnala)
                    response.status(404); // Not Found (o 500 per errore generico del server)
                    return gson.toJson(new ErrorResponse("Impossibile aggiornare lo stato. Utente non trovato o errore interno: " + email));
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                response.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("JSON malformato: " + e.getMessage()));
            } catch (Exception e) {
                response.status(500); // Internal Server Error
                // Logga l'eccezione per il debug lato server
                e.printStackTrace(); // È buona norma usare un logger più robusto in produzione
                return gson.toJson(new ErrorResponse("Errore interno del server: " + e.getMessage()));
            }
        });

        //Visualizza tutti i mezzi per l'admin
        get(baseURL + "/mezziAdmin", (req, res) -> {
            System.out.println("GET MEZZI ADMIN");

            ArrayList<Mezzo> mezzi = MezzoDao.getAllMezzi();

            if (mezzi.isEmpty()) {
                res.status(404); // Not Found
                return "Nessun mezzo trovato.";
            }

            res.status(200); // OK
            res.type("application/json");
            return new Gson().toJson(mezzi);
        });

        //Aggiorna i dati di un mezzo
        post(baseURL + "/updateMezzo", (request, response) -> {
            response.type("application/json");

            try {
                // Deserializza il JSON in arrivo direttamente in un oggetto Mezzo.
                Mezzo datiAggiornamento = gson.fromJson(request.body(), Mezzo.class);

                if (datiAggiornamento == null || datiAggiornamento.getId() <= 0) {
                    response.status(400); // Bad Request
                    return gson.toJson(new ErrorResponse("Payload mancante o malformato. Richiesti 'idMezzo'."));
                }

                boolean successo = MezzoDao.aggiornaMezzo(datiAggiornamento);

                if (successo) {
                    response.status(200); // OK
                    return gson.toJson(new SuccessResponse("Mezzo aggiornato con successo: " + datiAggiornamento.getId()));
                } else {
                    response.status(404); // Not Found
                    return gson.toJson(new ErrorResponse("Impossibile aggiornare il mezzo. ID non trovato: " + datiAggiornamento.getId()));
                }
            } catch (com.google.gson.JsonSyntaxException e) {
                response.status(400); // Bad Request
                return gson.toJson(new ErrorResponse("JSON malformato: " + e.getMessage()));
            } catch (Exception e) {
                response.status(500); // Internal Server Error
                e.printStackTrace(); // Log dell'errore per il debug
                return gson.toJson(new ErrorResponse("Errore interno del server: " + e.getMessage()));
            }
        });

        //Visualizza i parcheggi per l'admin
        get(baseURL + "/parcheggiAdmin", (req, res) -> {
            System.out.println("GET PARCHEGGI ADMIN");

            ArrayList<Parcheggio> parcheggi = ParcheggioDao.getAllParcheggi();

            if (parcheggi.isEmpty()) {
                res.status(404); // Not Found
                return "Nessun parcheggio trovato.";
            }

            res.status(200); // OK
            res.type("application/json");
            return new Gson().toJson(parcheggi);
        });

        //visualizza le corse per l'admin
        get(baseURL + "/corseAdmin", (req, res) -> {
            System.out.println("GET CORSE ADMIN");

            ArrayList<Corsa> corse = CorsaDao.getAllCorse();

            if (corse.isEmpty()) {
                res.status(404); // Not Found
                return "Nessuna corsa trovata.";
            }

            res.status(200); // OK
            res.type("application/json");
            return new Gson().toJson(corse);
        });

        //Visualizza i mezzi per l'utilizzatore
        get(baseURL + "/mezziUser", (req, res) -> {
            System.out.println("GET MEZZI");

            ArrayList<Mezzo> mezzi = MezzoDao.getMezziDisponibili();

            if (mezzi.isEmpty()) {
                res.status(404); // Not Found
                return "Nessun mezzo trovato.";
            }

            res.status(200); // OK
            res.type("application/json");
            return new Gson().toJson(mezzi);
        });

        //visualizza le corse per l'utilizzatore
        get(baseURL + "/corseUser", (req, res) -> {
            System.out.println("GET CORSE USER");

            String email = req.queryParams("email");
            ArrayList<Corsa> corse = CorsaDao.getAllCorseUser(email);

            if (corse.isEmpty()) {
                res.status(404); // Not Found
                return "Nessuna corsa trovata.";
            }

            res.status(200); // OK
            res.type("application/json");
            return new Gson().toJson(corse);
        });

        //Ricarica il credito di un utente
        post(baseURL + "/ricaricaCredito", (req, res) -> {

            System.out.println("POST RICARICA CREDITO");

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            String email = (String) body.get("email");
            Double importo = null;
            try {
                importo = Double.valueOf(body.get("importo").toString());
            } catch (Exception e) {
                res.status(400);
                return new ErrorResponse("Errore interno del server: " + e.getMessage());
            }

            boolean successo = UtenteDao.ricaricaCredito(email, importo);

            if (successo) {
                res.status(200); // OK
                //res.type("text/plain");
                return gson.toJson(new SuccessResponse("Importo aggiornato con successo per utente : " + email));
            } else {
                res.status(404); // Not Found
                return gson.toJson("Impossibile aggiornare il credito. Utente non trovato: " + email);
            }
        });

        //Aggiorna dati User
        get(baseURL + "/updateRicaricaUtente", (req, res) -> {
            System.out.println("GET UPDATE USER");

            String email = req.queryParams("email");

            Utente utente = UtenteDao.getUtenteUpdateRicarica(email);

            if (utente!= null) {
                res.status(200); // OK
                res.type("application/json");
                return new Gson().toJson(utente);
            } else {
                res.status(404); // Not Found
                return new Gson().toJson("Utente non trovato");
            }
        });

        post(baseURL + "/noleggiaMezzo", (req, res) -> {
            System.out.println("POST NOLEGGIO MEZZO");

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            String email = (String) body.get("email");
            String parcheggio = (String) body.get("parcheggio");
            Double idMezzoDouble = (Double) body.get("id_mezzo"); // Gson converte i numeri in Double
            int idMezzo = idMezzoDouble.intValue();

            if(UtenteDao.isUtenteSospeso(email)){
                res.status(403); // Forbidden
                return "Itente non autorizzato a noleggiare mezzi";
            }

            // Chiama DAO per aggiungere la corsa
            boolean succ = CorsaDao.addCorsa(email, parcheggio, idMezzo);

            if(succ){
                res.status(200);
                MezzoDao.setStatusMezzo(idMezzo, "IN_USO");
                return "Corsa avviata";
            }else{
                res.status(500);
                return "Errore durante l'avvio della corsa";
            }
        });
    }

    //DA RISOLVERE I CORS, PERCHÉ COME PRIMA NON FUNZIONAVA DI NUOVO
    private static void enableCORS() {
        final String CLIENT_ORIGIN = "http://localhost:3000";
        // Elenco dei metodi HTTP che il tuo server accetta
        final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS";
        // Elenco degli header che il client è autorizzato a inviare.
        // È buona norma riflettere quelli richiesti dal client (Access-Control-Request-Headers)
        // o elencarli esplicitamente se li conosci.
        // final String ALLOWED_HEADERS = "Content-Type, Authorization, X-Requested-With, Accept";


        // Filtro 'before' - eseguito PRIMA di ogni handler di rotta.
        // Imposta qui gli header che devono essere presenti su TUTTE le risposte CORS.
        before((request, response) -> {
            System.out.println(">>> CORS BEFORE filter running for: " + request.pathInfo() + " method: " + request.requestMethod());

            response.header("Access-Control-Allow-Origin", CLIENT_ORIGIN);
            response.header("Access-Control-Allow-Credentials", "true"); // Imposta su "true" se il tuo client invia credenziali (es. cookie, token di autorizzazione)
            // e la tua chiamata fetch ha { credentials: 'include' }

            System.out.println(">>> CORS BEFORE - Set Access-Control-Allow-Origin: " + CLIENT_ORIGIN);
            System.out.println(">>> CORS BEFORE - Set Access-Control-Allow-Credentials: true");

            // NON impostare qui Allow-Methods o Allow-Headers per le richieste OPTIONS,
            // se ne occuperà l'handler options("/*", ...) specifico.
        });

        // Handler specifico per le richieste OPTIONS (preflight)
        // Questo viene eseguito DOPO i filtri 'before' ma solo per le richieste OPTIONS.
        options("/*", (request, response) -> {
            System.out.println(">>> CORS OPTIONS handler running for: " + request.pathInfo());

            String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
                System.out.println(">>> CORS OPTIONS - Set Access-Control-Allow-Headers: " + accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
                System.out.println(">>> CORS OPTIONS - Set Access-Control-Allow-Methods: " + accessControlRequestMethod);
            } else {
                // Fallback se il client non invia Access-Control-Request-Method (improbabile per preflight valide)
                response.header("Access-Control-Allow-Methods", ALLOWED_METHODS);
                System.out.println(">>> CORS OPTIONS - Fallback Set Access-Control-Allow-Methods: " + ALLOWED_METHODS);
            }

            // Access-Control-Allow-Origin e Access-Control-Allow-Credentials
            // sono GIA' stati impostati dal filtro 'before'. NON impostarli di nuovo qui.

            response.status(204); // No Content - Risposta standard e corretta per OPTIONS
            System.out.println(">>> CORS OPTIONS - Responding with status 204");
            return "";
        });

        // Non è generalmente necessario un filtro 'after' se la configurazione 'before' e 'options' è corretta.
        // Potrebbe essere utile in scenari più complessi o per header specifici da aggiungere alla fine.
    }

    // Classi di supporto per le risposte JSON (possono essere esterne o interne)
    static class SuccessResponse {
        private String message;
        // private Object data; // Opzionale

        public SuccessResponse(String message) {
            this.message = message;
        }
        // public SuccessResponse(String message, Object data) { ... }
    }

    static class ErrorResponse {
        private String error;

        public ErrorResponse(String error) {
            this.error = error;
        }
    }

}
