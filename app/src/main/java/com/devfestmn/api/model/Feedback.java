package com.devfestmn.api.model;

/**
 * Feedback data POJO
 *
 * @author bherbst
 */
public class Feedback {
    private int speaker;
    private int content;
    private int recommendation;

    // Required default constructor for Firebase object mapping
    @SuppressWarnings("unused")
    private Feedback() {
    }

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
