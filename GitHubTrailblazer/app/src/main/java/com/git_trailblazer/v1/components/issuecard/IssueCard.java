package com.git_trailblazer.v1.components.issuecard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import com.git_trailblazer.v1.Helpers;
import com.git_trailblazer.v1.R;
import com.git_trailblazer.v1.data.Rating;

public class IssueCard extends LinearLayout {
    private Controller controller;

    // colors
    private static Integer colorUnselected;
    private static Integer colorStarSelected;
    private static Integer colorCommentSelected;
    private static Integer colorUpvoteSelected;
    private static Integer colorDownvoteSelected;
    private static Integer colorForkSelected;

    public IssueCard(Context context) {
        super(context);
        init(context);
    }

    public IssueCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public IssueCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public IssueCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    /**
     * Setup static fields
     * @param context - the context
     */
    private void init(Context context) {
        // init static color data
        if (colorUnselected == null) colorUnselected = ContextCompat.getColor(context, R.color.secondary3);
        if (colorStarSelected == null) colorStarSelected = ContextCompat.getColor(context, R.color.projectStar);
        if (colorCommentSelected == null) colorCommentSelected = ContextCompat.getColor(context, R.color.projectComment);
        if (colorUpvoteSelected == null) colorUpvoteSelected = ContextCompat.getColor(context, R.color.projectUpvote);
        if (colorDownvoteSelected == null) colorDownvoteSelected = ContextCompat.getColor(context, R.color.projectDownvote);
        if (colorForkSelected == null) colorForkSelected = ContextCompat.getColor(context, R.color.projectFork);
    }

    /**
     * Bind the model
     * @param model - the model
     * @return this instance
     */
    public IssueCard bindModel(Model model) {
        controller = new Controller(model.bindView(this));
        setOnClickListener(controller);
        return this;
    }

    /**
     * Update view
     * @param model - the model
     * @return this instance
     */
    IssueCard update(Model model) {
        // update title
        String title = model.getTitle();
        if (title != null) ((TextView)findViewById(R.id.issueCard__title)).setText(title);

        // update number
        String number = model.getNumber();
        if (number != null) ((TextView)findViewById(R.id.issueCard__number)).setText(number);

        // update time
        String createdAt = model.getCreatedAt();
        if (createdAt != null) ((TextView)findViewById(R.id.issueCard__time)).setText(createdAt);

        // update description
        String description = model.getDescription();
        TextView txt_description = findViewById(R.id.issueCard__description);
        if (description != null) {
            txt_description.setText(description);
            txt_description.setVisibility(VISIBLE);
        } else {
            txt_description.setVisibility(GONE);
        }

        // update service
        String service = model.getService();
        if (service != null) ((TextView)findViewById(R.id.issueCard__service)).setText(service);

        // update repository name
        String repoName = model.getRepoName();
        boolean repoIsHidden = repoName != null;
        if (repoIsHidden) {
            ((TextView)findViewById(R.id.issueCard__repoName)).setText(repoName);
            findViewById(R.id.issueCard__langContainer).setVisibility(VISIBLE);
            findViewById(R.id.issueCard__repoDetailContainer).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.issueCard__langContainer).setVisibility(GONE);
            findViewById(R.id.issueCard__repoDetailContainer).setVisibility(GONE);
        }

        // update language
        String language = model.getLanguage();
        if (language != null) {
            ((TextView)findViewById(R.id.issueCard__lang)).setText(language);
            findViewById(R.id.issueCard__langCirc)
                    .getBackground().setColorFilter(Helpers.getLanguageColor(getContext(), language), PorterDuff.Mode.SRC_ATOP);
            findViewById(R.id.issueCard__langContainer).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.issueCard__langContainer).setVisibility(GONE);
        }

        // update ratings
        String ratings = model.getRatings();
        if (ratings != null) ((TextView)findViewById(R.id.issueCard__rating)).setText(ratings);

        // update comments
        String comments = model.getComments();
        if (comments != null) ((TextView)findViewById(R.id.issueCard__comments)).setText(comments);

        // update stars
        String stars = model.getStars();
        if (stars != null) ((TextView)findViewById(R.id.issueCard__stars)).setText(stars);

        // update forks
        String forks = model.getForks();
        if (forks != null) ((TextView)findViewById(R.id.issueCard__forks)).setText(forks);

        // update rating
        Rating rating = model.getRating();
        if (rating != null) {
            ((ImageView)findViewById(R.id.issueCard__imgUpvote)).setImageTintList(rating == Rating.UPVOTE ? ColorStateList.valueOf(colorUpvoteSelected) : rating == Rating.DOWNVOTE ? ColorStateList.valueOf(colorDownvoteSelected) : ColorStateList.valueOf(colorUnselected));
        }

        // update user has commented
        Boolean isCommented = model.isCommented();
        if (isCommented != null) {
            ((ImageView)findViewById(R.id.issueCard__imgComment)).setImageTintList(isCommented ? ColorStateList.valueOf(colorCommentSelected) : ColorStateList.valueOf(colorUnselected));
        }

        // update user has starred
        Boolean isStarred = model.isStarred();
        if (isStarred != null) {
            ((ImageView)findViewById(R.id.issueCard__imgStar)).setImageTintList(isStarred ? ColorStateList.valueOf(colorStarSelected) : ColorStateList.valueOf(colorUnselected));
        }

        // update user has forked
        Boolean isForked = model.isForked();
        if (isForked != null) {
            ((ImageView)findViewById(R.id.issueCard__imgFork)).setImageTintList(isForked ? ColorStateList.valueOf(colorForkSelected) : ColorStateList.valueOf(colorUnselected));
        }

        return this;
    }
}
