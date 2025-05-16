package it.uniupo.pissir;

import it.uniupo.pissir.controller.registrazione.RegistrazioneController;
import spark.Spark;

import static spark.Spark.port;
import static spark.Spark.staticFileLocation;


public class Client {

    public static void main(String[] args) {

        //Cambio la porta di default
        port(5000);

        //Home


        //Registrazione
        Spark.get("/registrazione", RegistrazioneController.serveRegistrazionePage);//serve la pagina di registrazione
        Spark.put("/registrazione", RegistrazioneController.handleRegistrazionePut);//gestisce la registrazione dell'utente

    }
}
