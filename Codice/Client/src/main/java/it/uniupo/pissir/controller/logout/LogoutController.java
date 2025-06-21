package it.uniupo.pissir.controller.logout;

import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutController {

    /**
     *<h2>Gestisce logout</h2>
     *
     * <p>Rimuove l'utente dalla sessione e reindirizza alla home page.</p>
     */
    public static Route handleLogout = (Request req, Response res) -> {
        //Rimozione della sessione
        req.session().removeAttribute("utente");
        req.session().removeAttribute("admin");
        res.redirect("/");

        return null;
    };
}
