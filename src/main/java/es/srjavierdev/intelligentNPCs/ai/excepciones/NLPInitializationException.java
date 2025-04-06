package es.srjavierdev.intelligentNPCs.ai.excepciones;



public class NLPInitializationException extends Exception {
    public NLPInitializationException(String message) {
        super(message);
    }

    public NLPInitializationException(String message, Throwable cause) {
        super(message, cause);
    }
}