package mn.devfest.api.model;

/**
 * Feedback data POJO
 *
 * @author bherbst
 */
public class Feedback {
    private final int speaker;
    private final int content;
    private final int recommendation;


    public Feedback(int speaker, int content, int recommendation) {
        this.speaker = speaker;
        this.content = content;
        this.recommendation = recommendation;
    }

    public int getSpeaker() {
        return speaker;
    }

    public int getContent() {
        return content;
    }

    public int getRecommendation() {
        return recommendation;
    }
}
