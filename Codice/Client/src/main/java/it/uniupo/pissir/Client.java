package it.uniupo.pissir;

import spark.Spark;
import static spark.Spark.port;
import static spark.Spark.staticFileLocation;

//Import delle classi di controllo delle pagine
import it.uniupo.pissir.controller.index.IndexController;
import it.uniupo.pissir.controller.login.LoginController;
import it.uniupo.pissir.controller.registrazione.RegistrazioneController;

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

        //Login
        Spark.get("/login", LoginController.serveLoginPage);
    }
}
