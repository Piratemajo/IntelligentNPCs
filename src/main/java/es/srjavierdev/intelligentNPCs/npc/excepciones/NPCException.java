package es.srjavierdev.intelligentNPCs.npc.excepciones;


/**
 * Excepción personalizada para errores relacionados con NPCs en el plugin IntelligentNPCs
 */
public class NPCException extends Exception {
    private final String errorCode;
    private final String npcId;
    private final SeverityLevel severity;

    /**
     * Niveles de severidad para clasificar los errores
     */
    public enum SeverityLevel {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    // Constructores

    public NPCException(String message) {
        super(message);
        this.errorCode = "INPC-000";
        this.npcId = "unknown";
        this.severity = SeverityLevel.MEDIUM;
    }

    public NPCException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
        this.npcId = "unknown";
        this.severity = SeverityLevel.MEDIUM;
    }

    public NPCException(String message, String errorCode, String npcId) {
        super(message);
        this.errorCode = errorCode;
        this.npcId = npcId;
        this.severity = SeverityLevel.MEDIUM;
    }

    public NPCException(String message, String errorCode, String npcId, SeverityLevel severity) {
        super(message);
        this.errorCode = errorCode;
        this.npcId = npcId;
        this.severity = severity;
    }

    public NPCException(String message, Throwable cause, String errorCode, String npcId, SeverityLevel severity) {
        super(message, cause);
        this.errorCode = errorCode;
        this.npcId = npcId;
        this.severity = severity;
    }


    public String getErrorCode() {
        return errorCode;
    }

    public String getNpcId() {
        return npcId;
    }

    public SeverityLevel getSeverity() {
        return severity;
    }


    public String getFormattedError() {
        return String.format("[%s] NPC %s - %s (Severity: %s)",
                errorCode,
                npcId,
                getMessage(),
                severity.toString());
    }

    /**
     * Códigos de error comunes predefinidos
     */
    public static class ErrorCodes {
        public static final String CREATION_FAILED  = "INPC-001";
        public static final String NLP_INIT_FAILED = "INPC-002";
        public static final String MEMORY_ACCESS = "INPC-003";
        public static final String INVALID_PERSONALITY = "INPC-004";
        public static final String COMMAND_EXECUTION = "INPC-005";
        public static final String SAVE_FAILURE = "INPC-006";
        public static final String LOAD_FAILURE = "INPC-007";
    }
}