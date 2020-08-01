package com.example.githubtrailblazer.data;

/**
 * Rating enum
 */
public enum Rating {
    NONE(0),
    UPVOTE(1),
    DOWNVOTE(-1);

    final private int value;

    Rating(int value) {
        this.value = value;
    }

    public static Rating from(Integer valRated) {
        if (valRated == null) return NONE;
        switch (valRated) {
            case 1:
                return UPVOTE;
            case -1:
                return DOWNVOTE;
            default:
                return NONE;
        }
    }

    public int getValue() {
        return value;
    }
}
