package it.uniupo.pissir;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static spark.Spark.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;//Usata per le prove di stampa del json
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

        //Per poter accettare le richieste da qualunque dispositivo giri il client
        //ipAddress("0.0.0.0"); // Imposta l'indirizzo IP del server
        enableCORS();
        /*
        * Nelle pagine JS del client bisogna modificare le richieste in questo modo:
        *
        * 1-Modificare l'URL di base alla macchina dove gira il server, ad esempio:
        *
        *   ... fetch('http://192.168.0.7:4567/api/v1.0/mezziUser');
        *
        * 2-Modificare l'url in maniera dinamica, ad esempio:
        *
        *   const backendBaseUrl = `${window.location.hostname}:4567`;
        *   ... fetch(`http://${backendBaseUrl}/api/v1.0/mezziUser`);
        *
        * */


        //Login di un utente
        post( baseURL + "/login", (req, res) -> {

            System.out.println("POST Login");

            // Parso il JSON dal body
            Map<String, String> body = new Gson().fromJson(req.body(), Map.class);
            String email = body.get("email");
            String password = body.get("password");

            String tipo = UtenteDao.getTipoUtente(email, password);
            System.out.println(tipo);

            if (tipo == null) {
                res.status(401);
                return "Login fallito";
            }

            if (tipo.equals("user")) {
                Utente utente = UtenteDao.getUtenteBase(email, password, tipo);
                res.status(200);
                res.type("application/json");
                return new Gson().toJson(utente);
            } else if (tipo.equals("admin")) {
                Gestore gestore = UtenteDao.getUtenteAdmin(email, password, tipo);
                res.status(200);
                res.type("application/json");
                return new Gson().toJson(gestore);
            } else {
                res.status(401);
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

        //Aggiunge un nuovo mezzo
        post(baseURL + "/addMezzo", (req, res) -> {
            System.out.println("POST ADD MEZZO");

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);
            String tipo = (String) body.get("type");
            String parcheggio = (String) body.get("parking");
            double batteria = (body.get("battery") == null)? -1 : (double) body.get("battery");
            String codice_IMEI = (String) body.get("imei");

            System.out.println("tipo: " + tipo + "parcheggio: " + parcheggio + "batteria: " + batteria + "imei: " + codice_IMEI);;

            boolean successo = MezzoDao.addMezzo(tipo, (int) batteria, parcheggio, codice_IMEI);

            if (successo) {
                res.status(200); // OK
                //res.type("text/plain");
                return gson.toJson(new SuccessResponse("Mezzo aggiunto correttamente : "));
            } else {
                res.status(404); // Not Found
                return gson.toJson("Impossibile aggiungere il mezzo: ");
            }
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

        //aggiunge un nuovo parcheggio
        post(baseURL + "/addParcheggio", (req, res) -> {
            res.type("application/json");

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            String nome = (String) body.get("nome");
            double capienza = (double) body.get("capienzaMassima");
            String zona = (String) body.get("zona");

            boolean successo = ParcheggioDao.addParcheggio(nome, (int) capienza, zona);

            if (successo) {
                res.status(200); // OK
                //res.type("text/plain");
                return gson.toJson(new SuccessResponse("Parcheggio aggiunto correttamente : " + nome));
            } else {
                res.status(404); // Not Found
                return gson.toJson("Impossibile aggiungere il parcheggio: " + nome);
            }
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

        //Conversione punti in credito
        post(baseURL + "/conversionePunti", (req, res) -> {

            System.out.println("POST CNVERSIONE PUNTI");

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            String email = (String) body.get("email");
            double punti = (double) body.get("nuoviPunti");
            double importo = 0;

            try {
                importo = Double.valueOf(body.get("nuovoCredito").toString());
            } catch (Exception e) {
                res.status(400);
                return new ErrorResponse("Errore interno del server: " + e.getMessage());
            }

            System.out.println(email + " " + punti + " " + importo);
            boolean successo = UtenteDao.convertiPunti(email, (int) punti, importo);

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
        post(baseURL + "/updateUtente", (req, res) -> {

            System.out.println("POST UPDATE USER");

            String email = req.queryParams("email");

            //richiedo dati utente per aggiornare i punti e il credito
            Utente utente = UtenteDao.getDatiUtenteByMail(email);

            if (utente != null) {
                double credito = utente.getCredito();
                int punti = utente.getPunti();
                String stato = utente.getStato();
                res.status(200);
                res.type("application/json");

                Map<String, Object> responseMap = new HashMap<>();
                responseMap.put("credito", credito);
                responseMap.put("punti", punti);
                responseMap.put("stato", stato);
                return new Gson().toJson(responseMap);
            } else {
                res.status(404);
                return new Gson().toJson("Utente non trovato");
            }
        });

        //Richiesta noleggio di un mezzo (quindi aggiungo una corsa e modifico status mezzo in "IN_USO)
        post(baseURL + "/noleggiaMezzo", (req, res) -> {
            System.out.println("POST NOLEGGIO MEZZO");

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);

            String email = (String) body.get("email");
            String parcheggio = (String) body.get("parcheggio");
            Double idMezzoDouble = (Double) body.get("id_mezzo"); // Gson converte i numeri in Double
            int idMezzo = idMezzoDouble.intValue();

            if(UtenteDao.isUtenteSospeso(email)){
                res.status(403); // Forbidden
                return "Utente non autorizzato a noleggiare mezzi";
            }
            //Controllo prima che l'utilizzatore non abbia già una corsa in corso
            Corsa corsa = CorsaDao.getCorsaAttualeByEmail(email);
            if(corsa!=null){
                System.out.println("Nessuna corsa aggiunta.");
                System.out.println("L'utilizzatore con email " + email + " ha già una corsa in corso.");
                res.status(200);//vedere cosa è meglio restituire
                return "L'utilizzatore ha attualmente già una corsa. Non è possibile noleggiare un altro mezzo.";
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

        //Restituisce il Json con i dati relativa alla corsa attuale(da usare anche quando l'utente richiede di noleggiare, ma ha una corsa attuale)
        post(baseURL + "/corsaAttuale", (req, res) -> {
            System.out.println("POST CORSA IN CORSO");

            String email = req.queryParams("email");

            Corsa corsa = CorsaDao.getCorsaAttualeByEmail(email);

            if (corsa != null) {
                res.status(200); // OK
                res.type("application/json");

                Gson gsontmp = new GsonBuilder().setPrettyPrinting().create();
                String json = gsontmp.toJson(corsa);

                //Stampo il formato del Json per debug
                System.out.println("JSON restituito:\n" + json);
                return new Gson().toJson(corsa);
            } else {
                res.status(200); // Ok, ma Json vuoto
                return "{}";
            }
        });

        //termina e  paga la corsa che sista effettuando (link momentaneo get di esempio: http://localhost:4567/api/v1.0/terminaCorsa?id_corsa=17&parcheggio_fine=Parcheggio%20B)
        post(baseURL + "/terminaCorsa", (req,res) ->{

            System.out.println("POST TERMINA CORSA");

            int idCorsa = Integer.parseInt(req.queryParams("id_corsa"));
            String parcheggioFine = req.queryParams("parcheggio_fine");
            String guasto = req.queryParams("guasto");

            int idMezzo = CorsaDao.terminaCorsaById(idCorsa, parcheggioFine);

            if(idMezzo!=0){
                res.status(200);
                //Modifico lo stato del mezzo in "PRELEVABILE" o "NON_DISPONIBILE" a seconda che ci sia stato un guasto o meno
                if ("yes".equalsIgnoreCase(guasto)) {
                    MezzoDao.setStatusMezzo(idMezzo, "NON_DISPONIBILE");
                } else {
                    MezzoDao.setStatusMezzo(idMezzo, "PRELEVABILE");
                }
                return "Corsa terminata con successo";
            }else{
                res.status(404);
                return "Corsa non trovata o già terminata";
            }
        });

        //Parte MQTT per la gestione dei mezzi

        //visualizza la batteria di un mezzo
        get(baseURL + "/batteriaMezzo", (req, res) -> {
            System.out.println("GET BATTERIA MEZZO");

            int idMezzo = Integer.parseInt(req.queryParams("id_mezzo"));

            int batteria = MezzoDao.getBatteriaById(idMezzo);

            if (batteria != -1) {
                res.status(200); // OK
                return batteria;
            } else {
                res.status(404); // Not Found
                return "Mezzo non trovato o batteria non disponibile";
            }
        });

        // aggiorna la batteria di un mezzo
        put(baseURL + "/batteriaMezzo", (req, res) -> {
            System.out.println("POST BATTERIA MEZZO");

            int idMezzo = Integer.parseInt(req.queryParams("id_mezzo"));
            int nuovaBatteria = Integer.parseInt(req.queryParams("batteria"));

            boolean ok = MezzoDao.aggiornaBatteria(idMezzo, nuovaBatteria);

            if (ok) {
                res.status(200);
                return "Batteria aggiornata per il mezzo con ID " + idMezzo + ": " + nuovaBatteria + "%";
            } else {
                res.status(404);
                return "Mezzo non trovato o aggiornamento fallito";
            }
        });

        //aggiorna lo stato di un mezzo
        put(baseURL + "/statoMezzo", (req, res) -> {
            System.out.println("PUT STATO MEZZO");

            int idMezzo = Integer.parseInt(req.queryParams("id_mezzo"));

            String nuovoStato = req.queryParams("stato");


            boolean ok = false;
            if ("PRELEVABILE".equalsIgnoreCase(nuovoStato) || "NON_DISPONIBILE".equalsIgnoreCase(nuovoStato) || "IN_USO".equalsIgnoreCase(nuovoStato)) {
                ok = true;
                MezzoDao.setStatusMezzo(idMezzo, nuovoStato);
            }

            if (ok) {
                res.status(200);
                return "Stato aggiornato per il mezzo con ID " + idMezzo + ": " + nuovoStato;
            } else {
                res.status(404);
                return "Mezzo non trovato o aggiornamento fallito";
            }
        });

    }

    //DA RISOLVERE I CORS, PERCHÉ COME PRIMA NON FUNZIONAVA DI NUOVO
    /**
     * <h2>Abilita CORS per il server Spark (localhost:3000).</h2>
     * <p>
     *     Configura i filtri e gli handler per gestire le richieste CORS.
     *     In questo caso è impostata la configurazione per permettere le richieste solo dal client specifico, localhost:3000.
     * </p>
     */
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

        //Questa configurazione CORS permette al server di rispondere solo alle richieste provenienti da http://localhost:3000.
        //Questo per evitare che altri client possano fare richieste al server.

        //Nel caso bisognasse gestire eventuali credenziali (come i cookie) bisogna effettuare eventualmente una fetch così:
        /*
            fetch("http://<ip-del-server>:4567/api/...", {
                method: "POST",
                credentials: "include", // Necessario per cookie o sessioni
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(dati),
            });
        */
    }

    /**
     * <h2>Abilitazione CORS per il server Sparck (chiunque con gestione cookie).</h2>
     * <p>
     *   Questa funzione abilita CORS per il server Spark, permettendo richieste da qualsiasi origine.
     * </p>
     */
    private static void enableCORS_public() {
        final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS";

        before((request, response) -> {
            String origin = request.headers("Origin");
            if (origin != null) {
                response.header("Access-Control-Allow-Origin", origin); // Riflette l'origine che ha fatto la richiesta
                response.header("Access-Control-Allow-Credentials", "true");
            }
        });

        options("/*", (request, response) -> {
            String origin = request.headers("Origin");
            if (origin != null) {
                response.header("Access-Control-Allow-Origin", origin);
                response.header("Access-Control-Allow-Credentials", "true");

                String requestHeaders = request.headers("Access-Control-Request-Headers");
                if (requestHeaders != null) {
                    response.header("Access-Control-Allow-Headers", requestHeaders);
                } else {
                    response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
                }

                String requestMethod = request.headers("Access-Control-Request-Method");
                if (requestMethod != null) {
                    response.header("Access-Control-Allow-Methods", requestMethod);
                } else {
                    response.header("Access-Control-Allow-Methods", ALLOWED_METHODS);
                }

                response.status(204);
                return "";
            } else {
                response.status(403);
                return "CORS origin denied";
            }
        });
    }

    /**
     * <h2>Abilitazione CORS per il server Sparck (chiunque senza gestione cookie).</h2>
     * <p>
     *   Questa funzione abilita CORS per il server Spark, permettendo richieste da qualsiasi origine.
     * </p>
     */
    private static void enableCORS_public2() {
        final String ALLOWED_METHODS = "GET, POST, PUT, DELETE, OPTIONS";

        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*"); // Tutti i domini possono accedere
            response.header("Access-Control-Allow-Methods", ALLOWED_METHODS);
            response.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
        });

        options("/*", (request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", ALLOWED_METHODS);
            response.header("Access-Control-Allow-Headers", request.headers("Access-Control-Request-Headers") != null
                    ? request.headers("Access-Control-Request-Headers")
                    : "Content-Type, Authorization");
            response.status(204); // No Content
            return "";
        });
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
