package es.srjavierdev.intelligentNPCs.missions;


public enum MissionType {
    COLLECTION("Recolección", "Recolecta ciertos ítems del mundo"),
    HUNTING("Caza", "Derrota ciertos mobs o criaturas"),
    DELIVERY("Entrega", "Lleva un paquete a otro NPC"),
    EXPLORATION("Exploración", "Descubre un lugar específico"),
    CONSTRUCTION("Construcción", "Construye una estructura específica"),
    ESCORT("Escolta", "Acompaña a un NPC a un lugar seguro"),
    BOSS("Jefe", "Derrota a un jefe poderoso");

    private final String displayName;
    private final String description;

    MissionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static MissionType fromString(String text) {
        for (MissionType type : MissionType.values()) {
            if (type.displayName.equalsIgnoreCase(text) || type.name().equalsIgnoreCase(text)) {
                return type;
            }
        }
        return fromString(text);
    }
}