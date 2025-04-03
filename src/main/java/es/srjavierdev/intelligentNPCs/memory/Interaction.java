package es.srjavierdev.intelligentNPCs.memory;



import java.util.Objects;

public class Interaction {
    private final String input;
    private final String response;
    private final long timestamp;
    private int sentimentScore; // -1 negativo, 0 neutral, 1 positivo

    public Interaction(String input, String response) {
        this.input = input;
        this.response = response;
        this.timestamp = System.currentTimeMillis();
        this.sentimentScore = 0; // Se calcula después con NLP
    }

    public String getInput() {
        return input;
    }

    public String getResponse() {
        return response;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(int score) {
        this.sentimentScore = Math.max(-1, Math.min(1, score));
    }

    public boolean isPositive() {
        return sentimentScore > 0;
    }

    public boolean isNegative() {
        return sentimentScore < 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interaction that = (Interaction) o;
        return timestamp == that.timestamp &&
                Objects.equals(input, that.input) &&
                Objects.equals(response, that.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, response, timestamp);
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + input + " -> " + response +
                " (" + (sentimentScore > 0 ? "POS" : sentimentScore < 0 ? "NEG" : "NEU") + ")";
    }
}
