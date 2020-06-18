package com.example.githubtrailblazer.components.ProjectCard;

import android.content.Context;

/**
 * ProjectCard class
 */
public class ProjectCard {
    View view;
    Model model = new Model();

    /**
     * ProjectCard.Data class
     */
    public static class Data {
        public String name;
        public String language;
        public String description;
        public String profilePicUrl;
        public Integer rating;
        public Integer valRated;
        public Integer comments;
        public Boolean isCommented;
        public Integer stars;
        public Boolean isStarred;
        public Integer forks;
        public Boolean isForked;

        /**
         * Create project card data
         * @param name - project name
         * @param language - project language
         * @param description - project description
         * @param profilePicUrl - project profile photo url
         * @param rating - project rating
         * @param comments - project comments
         * @param stars - project stars
         * @param forks - project forks
         */
        public Data(String name, String language, String description, String profilePicUrl, Integer rating, Integer valRated, Integer comments, Boolean isCommented, Integer stars, Boolean isStarred, Integer forks,  Boolean isForked) {
            this.name = name;
            this.language = language;
            this.description = description;
            this.profilePicUrl = profilePicUrl;
            this.rating = rating;
            this.valRated = valRated;
            this.comments = comments;
            this.isCommented = isCommented;
            this.stars = stars;
            this.isStarred = isStarred;
            this.forks = forks;
            this.isForked = isForked;
        }
    }

    /**
     * Project.IOnReadyCB interface
     */
    public interface IOnReadyCB {
        /**
         * onready callback function
         * @param view - the project card view
         * @param index - the project card index
         */
        void exec(View view, int index);
    }

    /**
     * Create project card
     * @param context - the context
     */
    public ProjectCard(Context context, int index, Data data, IOnReadyCB onReadyCB) {
        view = new View(context, index, model, onReadyCB);
        model.setData(data);
    }
}
