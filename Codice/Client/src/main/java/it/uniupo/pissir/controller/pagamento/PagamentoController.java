package it.uniupo.pissir.controller.pagamento;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;
import spark.Response;
import spark.Route;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PagamentoController {

    /**
     * <h2>Gestisce l'aggiornamento del credito dopo il noleggio.</h2>
     *
     * <p>
     * Questa funzione gestisce il pagamento del noleggio da parte dell'utente.
     * Viene chiamata quando l'utente effettua un pagamento per un noleggio.
     * </p>
     *
     * @return Un messaggio di conferma se il pagamento è andato a buon fine, altrimenti un messaggio di errore.
     */
    public static Route handleAggiornamentoPuntiCreditoUtentePost = (Request req, Response res) -> {;

        System.out.println("Richiesta aggiornamento utente in sessione ...");

        Map<String, Object> sessioneUtente = req.session().attribute("utente");
        if (sessioneUtente == null) {
            res.status(401);
            return new Gson().toJson("Utente non autenticato");
        }

        String email = (String) sessioneUtente.get("email");
        String urlString = "http://localhost:4567/api/v1.0/updateUtente";

        System.out.println(email);

        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            String postData = "email=" + URLEncoder.encode(email, "UTF-8");

            OutputStream os = conn.getOutputStream();
            os.write(postData.getBytes("UTF-8"));
            os.flush();
            os.close();

            int status = conn.getResponseCode();
            if (status == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                conn.disconnect();

                // Parse JSON della risposta
                JsonObject json = new JsonParser().parse(content.toString()).getAsJsonObject();
                double credito = json.get("credito").getAsDouble();
                int punti = json.get("punti").getAsInt();
                String stato = json.get("stato").getAsString();

                // Aggiorna utente in sessione
                System.out.println("Nuovo credito: " + credito);
                System.out.println("Nuovi punti: " + punti);
                sessioneUtente.put("credito", credito);
                sessioneUtente.put("punti", punti);
                sessioneUtente.put("stato", stato);
                req.session().attribute("utente", sessioneUtente);

                res.status(200);
                return new Gson().toJson("Dati utente aggiornati con successo");
            } else {
                conn.disconnect();
                res.status(status);
                return new Gson().toJson("Errore durante aggiornamento utente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.status(500);
            return new Gson().toJson("Errore interno del server");
        }
    };
}
