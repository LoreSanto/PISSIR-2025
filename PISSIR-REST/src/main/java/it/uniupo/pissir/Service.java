package it.uniupo.pissir;
import com.google.gson.Gson;
import it.uniupo.pissir.oggetti.utente.RuoloUtente;
import it.uniupo.pissir.oggetti.utente.Utilizzatore;
import it.uniupo.pissir.oggetti.utente.UtenteDao;
import it.uniupo.pissir.oggetti.utente.Utilizzatore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static spark.Spark.*;

public class Service {

    public static void main(String[] args) {

        Gson gson = new Gson();

        String baseURL = "/api/v1.0";

        //Abilita CORS
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET, PUT, DELETE");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });


        //Ottieni singolo utente (da rivedere)
        /*get(baseURL + "/utente", "application/json", (req, res) -> {
            System.out.println("GET UTENTE");

            String mail = req.queryParams("mail");
            Utilizzatore utente = UtenteDao.getUtenteByEmail(mail);

            if(utente == null)
                halt(404);

            res.type("application/json");
            return utente;
        },gson::toJson);*/


        //aggiunta di un nuovo utente
        put(baseURL + "/utente", "application/json", (req, res)->{
            System.out.println("PUT UTENTE");
            Map addRequest = gson.fromJson(req.body(), Map.class);
            Utilizzatore utente;

            Map<String, Utilizzatore> finalJson = new HashMap<>();

            if(addRequest!=null && addRequest.containsKey("nome") && addRequest.containsKey("cognome") && addRequest.containsKey("email")&& addRequest.containsKey("password")) {

                String nome = String.valueOf(addRequest.get("nome"));
                String cognome = String.valueOf(addRequest.get("cognome"));
                String email = String.valueOf(addRequest.get("email"));
                String password = String.valueOf(addRequest.get("password"));

                //Aggiungi il nuovo utente al DB
                utente = UtenteDao.addUtente(new Utilizzatore(nome, cognome, email, password));

                //Se lo user non c'è ritorna 404
                if(utente == null)
                    halt(404);

                finalJson.put("task", utente);
                res.type("application/json");
                res.status(201); //Nuova risorsa aggiunta
            }
            else {
                halt(403);
            }

            return finalJson;

        },gson::toJson);


    }
}
