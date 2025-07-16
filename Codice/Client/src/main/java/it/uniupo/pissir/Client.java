package it.uniupo.pissir;

import it.uniupo.pissir.controller.corsa.CorsaController;
import it.uniupo.pissir.controller.gestore.GestoreController;
import it.uniupo.pissir.controller.mezzo.MezzoController;
import it.uniupo.pissir.controller.pagamento.PagamentoController;
import it.uniupo.pissir.controller.parcheggio.ParcheggioController;
import it.uniupo.pissir.controller.utente.UtenteController;
import spark.Spark;
import static spark.Spark.port;

//Import delle classi di controllo delle pagine
import it.uniupo.pissir.controller.index.IndexController;
import it.uniupo.pissir.controller.login.LoginController;
import it.uniupo.pissir.controller.registrazione.RegistrazioneController;
import it.uniupo.pissir.controller.profilo.ProfiloController;
import it.uniupo.pissir.controller.logout.LogoutController;

public class Client {

    public static void main(String[] args) {

        //Cambio la porta di default
        Spark.port(3000);
        //Directory da cui prenderà immagini, css e js
        Spark.staticFileLocation("/public");

        //Home
        Spark.get("/", IndexController.serveIndexPage);

        //Registrazione
        Spark.get("/register", RegistrazioneController.serveRegisterPage);
        Spark.post("/register", RegistrazioneController.handleRegistrazionePut);//Dobbiamo usare POST per la registrazione

        //Login
        Spark.get("/login", LoginController.serveLoginPage);
        Spark.post("/login", LoginController.handleLoginPost);//Trasformata per bene in post e controllata da java per la creazione della sessione

        //Profilo Utilizzatore
        Spark.get("/profilo", ProfiloController.serveProfiloPage);
        //da richiamare ogni qual volta si effettua un'azione che modifica la ricarica del profilo
        Spark.post("/aggiornaProfiloRicarica", ProfiloController.handleAggiornaProfiloRicaricaPost);
        Spark.post("/aggiornoPuntiCreditoUtente", PagamentoController.handleAggiornamentoPuntiCreditoUtentePost);
        //Check in locale per vedere se basta il credito
        Spark.post("/checkCreditoUtente", ProfiloController.handleCheckCreditoUtentePost);

        //Profilo Utilizzatore Sospeso
        Spark.get("/profiloSospeso", ProfiloController.serveProfiloSospesoPage);

        //Profilo Gestore
        Spark.get("/profiloAdmin", ProfiloController.serveProfiloAdminPage);

        //Logout
        Spark.get("/logout", LogoutController.handleLogout);

        //Gestione Utenti per l'Admin
        Spark.get("/users", GestoreController.serveUtentiPage);

        //Gestione Mezzi per l'Admin
        Spark.get("/mezziAdmin", MezzoController.serveMezziAdminPage);

        //Gestione Parcheggi per l'Admin
        Spark.get("/parcheggiAdmin", ParcheggioController.serveParcheggiPage);

        //Visualizzazione Corse per l'Admin
        Spark.get("/corseAdmin", CorsaController.serveCorsePage);

        //Visualizza Mezzi per Utilizzatore
        Spark.get("/mezziUser", MezzoController.serveMezziPage);

        //Visualizza Corse per Utilizzatore
        Spark.get("/corseUser", CorsaController.serveCorseUserPage);

        //Visualizza Corsa attuale per Utilizzatore
        Spark.get("/corsaAttuale", CorsaController.serveCorsaAttualePage);

        //Richiesta di noleggio di un mezzo
        //Per il momento è gestito dal server la possibilità che l'utente abbia già noleggiato un mezzo
        Spark.post("/noleggioMezzo", MezzoController.handleRichiestaNoleggioPost);

        //Richiesta mail della sessione (da mettere anche in altre operazioni dove richiede la mail)
        Spark.get("/sessione/mail", UtenteController.getSessionEmail);
    }
}
