package com.project.excepcions;

public class IOFitxerExcepcio extends Exception {
    // Constructor que accepta només un missatge
    public IOFitxerExcepcio(String message) {
        super(message);
    }

    // Constructor que accepta un missatge i una causa (Throwable)
    public IOFitxerExcepcio(String message, Throwable cause) {
        super(message, cause);
    }
}
