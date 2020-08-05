package com.example.githubtrailblazer.components.repocard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.data.Rating;
import com.example.githubtrailblazer.data.RepoCardData;
import com.squareup.picasso.Picasso;

public class RepoCard extends LinearLayout {
    private Controller controller;

    // colors
    private static Integer colorUnselected;
    private static Integer colorStarSelected;
    private static Integer colorCommentSelected;
    private static Integer colorUpvoteSelected;
    private static Integer colorDownvoteSelected;
    private static Integer colorForkSelected;

    public RepoCard(Context context) {
        super(context);
        init(context);
    }

    public RepoCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public RepoCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public RepoCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        // init static color data
        if (colorUnselected == null) colorUnselected = ContextCompat.getColor(context, R.color.secondary3);
        if (colorStarSelected == null) colorStarSelected = ContextCompat.getColor(context, R.color.projectStar);
        if (colorCommentSelected == null) colorCommentSelected = ContextCompat.getColor(context, R.color.projectComment);
        if (colorUpvoteSelected == null) colorUpvoteSelected = ContextCompat.getColor(context, R.color.projectUpvote);
        if (colorDownvoteSelected == null) colorDownvoteSelected = ContextCompat.getColor(context, R.color.projectDownvote);
        if (colorForkSelected == null) colorForkSelected = ContextCompat.getColor(context, R.color.projectFork);
    }

    public RepoCard bindModel(Model model) {
        controller = new Controller(model.bindView(this));
        findViewById(R.id.projectCard__btnUpvote).setOnClickListener(controller);
        findViewById(R.id.projectCard__btnDownvote).setOnClickListener(controller);
        findViewById(R.id.projectCard__btnComment).setOnClickListener(controller);
        findViewById(R.id.projectCard__btnStar).setOnClickListener(controller);
        findViewById(R.id.projectCard__btnFork).setOnClickListener(controller);
        findViewById(R.id.projectCard__btnActions).setOnClickListener(controller);
        setOnClickListener(controller);
        return this;
    }

    RepoCard update(Model model) {
        // update profile pic
        String profilePicUrl = model.getProfilePicUrl();
        if (profilePicUrl != null)
            new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get()
                                .load(profilePicUrl)
                                .into(((ImageView)findViewById(R.id.projectCard__profilePic)));
                    }
                });

        // update service
        String service = model.getService();
        if (service != null) ((TextView)findViewById(R.id.projectCard__service)).setText(service);

        // update name
        String name = model.getName();
        if (name != null) ((TextView)findViewById(R.id.projectCard__name)).setText(name);

        // update language
        String language = model.getLanguage();
        if (language != null) {
            ((TextView)findViewById(R.id.projectCard__lang)).setText(language);
            findViewById(R.id.projectCard__langCirc)
                    .getBackground().setColorFilter(Helpers.getLanguageColor(getContext(), language), PorterDuff.Mode.SRC_ATOP);
            findViewById(R.id.projectCard__langContainer).setVisibility(VISIBLE);
        } else {
            findViewById(R.id.projectCard__langContainer).setVisibility(GONE);
        }

        // update description
        String description = model.getDescription();
        TextView txt_description = findViewById(R.id.projectCard__desc);
        if (description != null) {
            txt_description.setText(description);
            txt_description.setVisibility(VISIBLE);
        } else {
            txt_description.setVisibility(GONE);
        }

        // update rating
        String ratings = model.getRatings();
        if (ratings != null) ((TextView)findViewById(R.id.projectCard__rating)).setText(ratings);

        // update comments
        String comments = model.getComments();
        if (comments != null) ((TextView)findViewById(R.id.projectCard__comments)).setText(comments);

        // update stars
        String stars = model.getStars();
        if (stars != null) ((TextView)findViewById(R.id.projectCard__stars)).setText(stars);

        // update forks
        String forks = model.getForks();
        if (forks != null) ((TextView)findViewById(R.id.projectCard__forks)).setText(forks);

        // update upvotes / downvotes
        Rating rating = model.getRating();
        if (rating != null) {
            ((ImageView)findViewById(R.id.projectCard__btnUpvote)).setImageTintList(rating == Rating.UPVOTE ? ColorStateList.valueOf(colorUpvoteSelected) : ColorStateList.valueOf(colorUnselected));
            ((ImageView)findViewById(R.id.projectCard__btnDownvote)).setImageTintList(rating == Rating.DOWNVOTE ? ColorStateList.valueOf(colorDownvoteSelected) : ColorStateList.valueOf(colorUnselected));
        }

        // update user has commented
        Boolean isCommented = model.isCommented();
        if (isCommented != null) {
            ((ImageView)findViewById(R.id.projectCard__btnComment)).setImageTintList(isCommented ? ColorStateList.valueOf(colorCommentSelected) : ColorStateList.valueOf(colorUnselected));
        }

        // update user has starred
        Boolean isStarred = model.isStarred();
        if (isStarred != null) {
            ((ImageView)findViewById(R.id.projectCard__btnStar)).setImageTintList(isStarred ? ColorStateList.valueOf(colorStarSelected) : ColorStateList.valueOf(colorUnselected));
        }

        // update user has forked
        Boolean isForked = model.isForked();
        if (isForked != null) {
            ((ImageView)findViewById(R.id.projectCard__btnFork)).setImageTintList(isForked ? ColorStateList.valueOf(colorForkSelected) : ColorStateList.valueOf(colorUnselected));
        }

        return this;
    }

    RepoCard showActions() {
        // create popup
        Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, findViewById(R.id.projectCard__btnActions));
        popupMenu.setOnMenuItemClickListener(controller);
        popupMenu.inflate(R.menu.actions_menu);

        // add icons
        MenuPopupHelper menuHelper = new MenuPopupHelper(wrapper, (MenuBuilder) popupMenu.getMenu(), findViewById(R.id.projectCard__btnActions));
        menuHelper.setForceShowIcon(true);
        menuHelper.setGravity(Gravity.END);
        menuHelper.show();
        return this;
    }
}
