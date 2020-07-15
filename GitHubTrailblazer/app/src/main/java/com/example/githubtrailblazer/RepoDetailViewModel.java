package com.example.githubtrailblazer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.githubtrailblazer.components.ProjectCard.Model;

public class RepoDetailViewModel extends ViewModel {
    // Observable component that triggers UI updates when data
    int didVote = 0;
    boolean didStar = false;
    boolean didFork = false;
    boolean didComment = false;

    MutableLiveData<Integer> upvotes;
        public MutableLiveData<Integer> getUpvotes() {
            if (upvotes == null) {
                upvotes = new MutableLiveData<Integer>();
            }
            return upvotes;
        }
    MutableLiveData<Integer> comments;
        public MutableLiveData<Integer> getComments() {
            if (comments == null) {
                comments = new MutableLiveData<Integer>();
            }
            return comments;
        }
    MutableLiveData<Integer> stars;
        public MutableLiveData<Integer> getStars() {
            if (stars == null) {
                stars = new MutableLiveData<Integer>();
            }
            return stars;
        }
    MutableLiveData<Integer> forks;
        public MutableLiveData<Integer> getForks() {
            if (forks == null) {
                forks = new MutableLiveData<Integer>();
            }
            return forks;
        }

    /**
     * Perform upvote
     */
    void upvote() {
        switch (didVote) {
            case -1:
                didVote = 1;
                getUpvotes().setValue(getUpvotes().getValue() + 2);
                break;
            case 0:
                didVote = 1;
                getUpvotes().setValue(getUpvotes().getValue() + 1);
                break;
            case 1:
                didVote = 0;
                getUpvotes().setValue(getUpvotes().getValue() - 1);
                break;
        }
    }

    /**
     * Perform downvote
     */
    void downvote() {
        switch (didVote) {
            case -1:
                didVote = 0;
                getUpvotes().setValue(getUpvotes().getValue() + 1);
                break;
            case 0:
                didVote = -1;
                getUpvotes().setValue(getUpvotes().getValue() - 1);
                break;
            case 1:
                didVote = -1;
                getUpvotes().setValue(getUpvotes().getValue() - 2);
                break;
        }
    }

    /**
     * Perform star
     */
    void star() {
        didStar = !didStar;
        int stars = getStars().getValue();
        getStars().setValue(didStar ? stars+1 : stars-1);
    }

    /**
     * Perform fork
     */
    void fork() {
        didFork = !didFork;
        int forks = getForks().getValue();
        getForks().setValue(didFork ? forks+1 : forks-1);
    }

}
