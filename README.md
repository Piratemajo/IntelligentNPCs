# 📜 IntelligentNPCs

> [!CAUTION]
> Este Proyecto esta en una fase Muy Beta


> [!NOTE]
> Próximamente actulizacion del Proyecto y mucho más.

![Versión](https://img.shields.io/badge/Versión-1.5.1-blue)
![Paper](https://img.shields.io/badge/Paper-1.20.1-green)

Plugin de Minecraft de NPCs inteligentes con redes neuronales, sistema de misiones procedurales y diálogos dinámicos.

## 📌 Características Principales

- 🧠 NPCs con comportamiento basado en redes neuronales
- 📜 Sistema de misiones procedurales generadas dinámicamente
- 💬 Diálogos naturales con procesamiento de lenguaje (NLP)
- 🏆 Sistema de reputación y memoria de interacciones
- 💰 Integración con economía (Vault)
- 🛠️ Altamente configurable

## 📦 Instalación

1. Descarga el archivo `.jar` desde [Releases](#)
2. Colócalo en la carpeta `plugins/` de tu servidor
3. Reinicia el servidor
4. Configura según tus necesidades en `plugins/IntelligentNPCs/config.yml`

## 🧩 Dependencias

| Plugin | Obligatorio | Descripción |
|--------|-------------|-------------|
| Citizens | ✅ | Para el sistema de NPCs |
| Vault | ❌ (Opcional) | Para economía |
| PlaceholderAPI | ❌ | Para integración con otros plugins |

## 🛠 Uso Básico

```bash
      /intelligentnpc create <nombre> <personalidad> - Crea un nuevo NPC
      /intelligentnpc delete - Elimina el NPC seleccionado
      /intelligentnpc talk - Habla con el NPC seleccionado
      /intelligentnpc list - Lista todos los NPCs
      /intelligentnpc delete - Borra el NPC que estas mirando
      /intelligentnpc reload - Recarga la configuración
```

## 📐 Diagrama de Clases

```mermaid
classDiagram
    %% -------------------------------
    %% Interfaces Principales
    %% -------------------------------
    class IntelligentNPC {
        <<interface>>
        +startConversation(Player player): void
        +processPlayerInput(Player player, String input): void
        +endConversation(Player player): void
        +offerMission(Player player): void
    }

    class MemorySystem {
        <<interface>>
        +recordInteraction(UUID playerId, String input, String response): void
        +getPlayerMemory(UUID playerId): PlayerMemory
        +cleanOldMemories(int daysToKeep): void
    }

    class MissionGenerator {
        <<interface>>
        +generateMission(Player player, int reputation): Mission
        +getAvailableMissionTypes(Player player): Set~MissionType~
    }

    class ReputationSystem {
        <<interface>>
        +adjustReputation(UUID npcId, UUID playerId, int amount): void
        +getReputationLevel(UUID npcId, UUID playerId): int
        +getGlobalReputation(UUID playerId): int
    }

    class NeuralNetwork {
        <<interface>>
        +train(List~Training~ training): void
        +predict(String input): String
        +saveModel(String path): void
        +loadModel(String path): void
    }

    %% -------------------------------
    %% Implementaciones Principales
    %% -------------------------------
    class EnhancedNeuralNPC {
        -npcId: UUID
        -personality: String
        -currentConversations: ConcurrentHashMap~UUID, Conversation~
        +startConversation(Player player): void
        +processPlayerInput(Player player, String input): void
        +completeMission(Player player): void
        -generateResponse(ParsedMessage message): String
    }

    class NeuralNetwork {
        -model: MultilayerNetwork
        -tokenizer: NlpTokenizer
        +trainOnline(String input, String expectedOutput): void
        +batchTrain(List~Training~ training): void
        -preprocessInput(String text): float[]
    }

    class ProceduralMissionGenerator {
        -rewardTemplates: EnumMap~MissionType, RewardTemplate~
        -difficultyCurve: DifficultyCalculator
        +generateMission(Player player, int reputation): Mission
        +generateDailyMission(UUID npcId): Mission
        -selectRewards(MissionType type, int difficulty): List~ItemStack~
    }

    class NPCMemory {
        -memoryStore: MemoryDatabase
        -playerMemories: Cache~UUID, PlayerMemory~
        +saveToDisk(): void
        +loadFromDisk(): void
        -compressOldMemories(): void
    }

    class ReputationSystem {
        -reputationData: Map~UUID, Map~UUID, Integer~~
        -eventListeners: List~ReputationListener~
        +addListener(ReputationListener listener): void
        +recalculateGlobalReputation(UUID playerId): void
    }

    %% -------------------------------
    %% Modelos de Datos
    %% -------------------------------
    class Mission {
        -missionId: UUID
        -npcId: UUID
        -missionType: MissionType
        -objectives: List~String~
        -rewards: MissionReward
        -difficulty: int
        +isCompleted(): boolean
        +getProgress(): float
        +awardPlayer(Player player): void
    }

    class PlayerMemory {
        -playerId: UUID
        -interactionHistory: CircularBuffer~Interaction~
        -completedMissions: List~MissionRecord~
        -preferences: PlayerPreferences
        +getLastInteraction(): Optional~Interaction~
        +getMissionCompletionCount(MissionType type): int
    }

    class Conversation {
        -conversationId: UUID
        -startTime: Instant
        -lastActivity: Instant
        -messages: List~DialogueMessage~
        +isActive(): boolean
        +addMessage(DialogueMessage message): void
        +timeoutInSeconds(): long
    }

    class ParsedMessage {
        -rawInput: String
        -tokens: List~String~
        -sentiment: SentimentScore
        -namedEntities: Map~String, String~
        -intent: String
        +getMainIntent(): String
        +containsEntity(String type): boolean
    }

    class MissionReward {
        -money: double
        -items: List~ItemStack~
        -experience: int
        -reputationGain: int
        +applyReward(Player player): void
    }

    %% -------------------------------
    %% Enums y Tipos de Datos
    %% -------------------------------
    class MissionType {
        <<enum>>
        COLLECTION
        HUNTING
        DELIVERY
        EXPLORATION
        BOSS
        ESCORT
        +getDisplayName(): String
    }

    class SentimentScore {
        <<enum>>
        POSITIVE(0.7, 1.0)
        NEUTRAL(0.3, 0.7)
        NEGATIVE(0.0, 0.3)
        +score: double
        +isPositive(): boolean
    }

    %% -------------------------------
    %% Relaciones de Implementación
    %% -------------------------------
    EnhancedNeuralNPC ..|> IIntelligentNPC
    NeuralNetwork ..|> INeuralNetwork
    ProceduralMissionGenerator ..|> IMissionGenerator
    NPCMemory ..|> IMemorySystem
    ReputationSystem ..|> IReputationSystem

    %% -------------------------------
    %% Relaciones de Composición
    %% -------------------------------
    EnhancedNeuralNPC *-- NeuralNetwork
    EnhancedNeuralNPC *-- NPCMemory
    EnhancedNeuralNPC *-- ReputationSystem
    EnhancedNeuralNPC o-- Mission
    NPCMemory *-- PlayerMemory
    PlayerMemory *-- Interaction
    ProceduralMissionGenerator *-- MissionReward
    Mission *-- MissionReward
    Conversation *-- DialogueMessage
    NeuralNetwork *-- NlpTokenizer

    %% -------------------------------
    %% Relaciones de Dependencia
    %% -------------------------------
    EnhancedNeuralNPC ..> ParsedMessage
    ProceduralMissionGenerator ..> MissionType
    NeuralNetwork ..> Training
    ReputationSystem ..> ReputationEvent
    Mission ..> ItemStack
    PlayerMemory ..> MissionRecord

    %% -------------------------------
    %% Notas Adicionales
    %% -------------------------------
    note for EnhancedNeuralNPC "Gestiona conversaciones\ny memoria por jugador"
    note for NeuralNetwork "Modelo LSTM entrenable\nen tiempo real"
    note for ProceduralMissionGenerator "Genera misiones basadas\nen nivel y reputación"
    note for NPCMemory "Almacenamiento persistente\ncon cache LRU"
```

## 🔧 Configuración

Edita `config.yml` para personalizar:

```yaml
# Configuración de IntelligentNPCs - Versión 1.6
settings:
  # Configuración general del plugin
  debug-mode: false
  save-interval: 300  # Segundos entre guardados automáticos
  max-conversation-length: 20  # Aumentado de 15 a 20 mensajes
  default-personality: "neutral"

# Configuración de NPCs
npcs:
  personalities:
    warrior:
      display-name: "Guerrero"
      response-speed: 1.2
      aggression-level: 0.7
      mission-chance: 0.6
      nlp-model: "warrior_model.ser"  # Nuevo: Modelo específico
      response-style: "directo"  # Nuevo
    scholar:
      display-name: "Erudito"
      response-speed: 0.8
      aggression-level: 0.1
      mission-chance: 0.4
      nlp-model: "scholar_model.ser"
      response-style: "detallado"
    merchant:
      display-name: "Mercader"
      response-speed: 1.0
      aggression-level: 0.3
      mission-chance: 0.8
      nlp-model: "merchant_model.ser"
      response-style: "comercial"
    neutral:
      display-name: "Neutral"
      response-speed: 1.0
      aggression-level: 0.5
      mission-chance: 0.5
      nlp-model: "neutral_model.ser"
    friendly:  # Nueva personalidad
      display-name: "Amigable"
      response-speed: 1.1
      aggression-level: 0.2
      mission-chance: 0.7
      nlp-model: "friendly_model.ser"
      response-style: "amable"

  # Configuración de spawn
  spawn:
    max-npcs-per-chunk: 2
    spawn-chance: 0.15
    min-player-level: 5  # Nuevo: Nivel mínimo para spawn

# Configuración de NLP 
nlp:
  enabled: true
  models-directory: "plugins/IntelligentNPCs/models"
  cache-size: 1500  # Aumentado de 1000
  processing-timeout: 5000  # ms
  max-text-length: 250  # Caracteres máximos a procesar
  language-packs:  # Nuevo: Soporte multiidioma
    es_ES:
      core: "stanford-spanish.jar"
      models: "spanish-models.jar"
    en_US:
      core: "stanford-english.jar"
      models: "english-models.jar"
  fallback-language: "en_US"  # Idioma alternativo

missions:
  difficulty-settings:
    min-level: 1
    max-level: 15  # Aumentado de 10
    level-multiplier: 0.6  # Ajustado de 0.5
    dynamic-difficulty: true  # Nuevo: Ajusta según habilidad jugador

  reward-settings:
    base-money-reward: 5.0
    money-multiplier: 1.5
    item-reward-chance: 0.65
    xp-reward-base: 10
    reputation-rewards:  # Nuevo
      completion: 5
      bonus-objectives: 2
      time-bonus: 3

  cooldowns:
    between-missions: 1200
    same-player-mission: 3600
    same-type-mission: 1800  # Nuevo: Cooldown por tipo

# Configuración de diálogos 
dialogue:
  greeting-variations: 8  # Aumentado de 5
  response-timeout: 30
  memory-size: 15  # Aumentado de 10
  max-response-time: 3  # Segundos máximos para generar respuesta

  # Palabras clave mejoradas
  triggers:
    mission-request: ["misión", "tarea", "trabajo", "quest", "encargo"]
    help-request: ["ayuda", "socorro", "problema", "emergencia"]
    trade-request: ["comprar", "vender", "intercambio", "comercio", "negocio"]
    nlp-triggers:  # Nuevo: Para funciones avanzadas
      sentiment: ["feliz", "triste", "enojado", "emocionado"]
      questions: ["cómo", "qué", "dónde", "por qué"]

# Configuración de reputación 
reputation:
  base-gain: 1
  base-loss: 2
  mission-complete-gain: 5
  mission-fail-loss: 3
  daily-decay: 1  # Nuevo: Pérdida diaria de reputación
  levels:
    -100: "Enemigo"
    -75: "Hostil"
    -50: "Desconfiado"
    -25: "Receloso"
    0: "Neutral"
    25: "Conocido"
    50: "Aliado"
    75: "Amigo"
    100: "Héroe"
  effects:  # Nuevo: Efectos por nivel
    -50:
      mission-rewards: 0.7
      dialogue-options: 5
    50:
      mission-rewards: 1.3
      dialogue-options: 10

# Configuración de economía mejorada
economy:
  enabled: true
  currency-symbol: "$"
  reward-multiplier: 1.0
  penalty-multiplier: 0.5
  inflation-adjustment: 0.95  # Nuevo: Ajuste por inflación
  min-reward: 2.0  # Nuevo: Mínimo garantizado

# Integraciones ampliadas
integrations:
  Citizens:
    enabled: true
    min-version: "2.0.30"
  Vault:
    enabled: true
    economy: true
    permissions: false
  PlaceholderAPI:
    enabled: false
    auto-register: true
  WorldGuard:
    enabled: true
    region-blacklist: ["npc-restricted"]  # Nuevo
  MythicMobs:  # Nueva integración
    enabled: false
    boss-missions: true

# Mensajes personalizables ampliados
messages:
  greetings:
    - "¡Saludos, {player}!"
    - "¿En qué puedo ayudarte hoy?"
    - "Hola, ¿qué tal?"
    - "¡Buen día, aventurero!"
    - "Vaya, un visitante. ¿Necesitas algo?"
  mission-offer:
    - "Tengo una misión para ti: {mission_objective}"
    - "¿Podrías ayudarme con {mission_objective}?"
    - "Necesito a alguien para {mission_objective}, ¿te interesa?"
    - "Hay una tarea disponible: {mission_objective}. ¿Aceptas?"
  reputation-change:
    positive: "¡Tu reputación con {npc_name} ha mejorado! (+{amount})"
    negative: "Tu reputación con {npc_name} ha empeorado... ({amount})"
  errors:
    no-mission: "No tengo ninguna misión para ti ahora mismo."
    cooldown: "Vuelve más tarde, no tengo nada para ti todavía."
    no-permission: "No pareces estar preparado para esto."
    nlp-failure: "No entiendo lo que dices. ¿Podrías reformularlo?"  # Nuevo
    command-error: "Error al ejecutar el comando: {error_code}"  # Nuevo

# Configuración avanzada mejorada
advanced:
  performance:
    async-processing: true
    max-threads: 4
    thread-timeout: 30  # Segundos
  logging:
    conversation-logs: false
    save-format: "json"
    log-errors-to-file: true  # Nuevo
    max-log-size: 10  # MB
  backup:
    auto-backup: true  # Nuevo
    backup-interval: 86400  # 24 horas en segundos
    max-backups: 7
```

## 📄 Licencia

Este proyecto está bajo la licencia MIT. Ver [LICENSE](LICENSE) para más detalles.

## 🧐 Futuras Actulizaciones del Proyecto

1. En el proyecto ya tengo varios metodos preparados y funciones poco a poco ire metiendolas en el proyecto.
2. Compatibilidad con SQL.
3. Compatibilidad con mas software de minecraft (Folia de papermc).

## 💻 Servidor de Pruebas

- IP: Proximamente...
