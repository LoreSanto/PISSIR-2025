package it.uniupo.pissir;

import it.uniupo.pissir.controller.corsa.CorsaController;
import it.uniupo.pissir.controller.gestore.GestoreController;
import it.uniupo.pissir.controller.mezzo.MezzoController;
import it.uniupo.pissir.controller.parcheggio.ParcheggioController;
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
        Spark.post("/login", LoginController.handleLoginPost);

        //Profilo Utilizzatore
        Spark.get("/profilo", ProfiloController.serveProfiloPage);
        Spark.post("/aggiornaProfiloRicarica", ProfiloController.handleAggiornaProfiloRicaricaPost);

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
        Spark.post("/noleggioMezzo", MezzoController.handleRichiestaNoleggioPost);
    }
}
