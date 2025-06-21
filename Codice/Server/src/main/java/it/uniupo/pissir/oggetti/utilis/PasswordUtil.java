package it.uniupo.pissir.oggetti.utilis;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    /**
     * <h2>Cifra una password</h2>
     *
     * <p>
     *     Utilizza BCrypt per cifrare una password in modo sicuro.
     * </p>
     *
     * @param plainTextPassword la password in chiaro da cifrare
     * @return password cifrata
     *
    */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * <h2>Decifra una password</h2>
     *
     * <p>
     *     Utilizza BCrypt per cifrare una password in modo sicuro.
     * </p>
     *
     * @param plainTextPassword la password in chiaro da cifrare
     * @return password cifrata
     *
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}
