package com.example.githubtrailblazer;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.githubtrailblazer.data.RepoCardData;

/**
 * RepoDetailActivity class
 */
public class RepoDetailActivity extends AppCompatActivity {
    private LinearLayout container;

    private RepoDetailViewModel viewModel;

    private RepoCardData data;
    private int colorUnselected;
    private int colorStarSelected;
    private int colorCommentSelected;
    private int colorUpvoteSelected;
    private int colorDownvoteSelected;
    private int colorForkSelected;

    private TextView usernameTextView;
    private TextView reponameTextView;
    private TextView descriptionTextView;
    private TextView languageTextView;
    private TextView readmeTextView;

    private TextView upvoteTextView;
    private TextView commentTextView;
    private TextView starTextView;
    private TextView forkTextView;

    private ImageButton upvoteBtn;
    private ImageButton downvoteBtn;
    private ImageButton commentBtn;
    private ImageButton starBtn;
    private ImageButton forkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repodetail);

        // Get the data passed from RepoCard
        Intent intent = getIntent();
        data = (RepoCardData) intent.getSerializableExtra("data");

        // init colors
        colorUnselected = ContextCompat.getColor(this, R.color.secondary6);
        colorStarSelected = ContextCompat.getColor(this, R.color.projectStar);
        colorCommentSelected = ContextCompat.getColor(this, R.color.projectComment);
        colorUpvoteSelected = ContextCompat.getColor(this, R.color.projectUpvote);
        colorDownvoteSelected = ContextCompat.getColor(this, R.color.projectDownvote);
        colorForkSelected = ContextCompat.getColor(this, R.color.projectFork);

        // Assign UI elements
        usernameTextView = findViewById(R.id.repodetail_user_id);
        reponameTextView = findViewById(R.id.repodetail_name_txt);
        descriptionTextView = findViewById(R.id.repodetail_description_txt);
        languageTextView = findViewById(R.id.repodetail_lang_txt);
        readmeTextView = findViewById(R.id.repodetail_readme);

        reponameTextView.setText(data.name);
        descriptionTextView.setText(data.description);
        languageTextView.setText(data.language);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            readmeTextView.setText(Html.fromHtml("<h1 align=\"center\"> Awesome Python </h1> <br>\n<p align=\"center\">\n  <a href=\"https://gitpoint.co/\">\n  </a>\n</p>\n\n<p align=\"center\">\n A curated list of awesome Python frameworks, libraries, software and resources. \n</p>\n\n<p align=\"center\">\n  <a href=\"https://itunes.apple.com/us/app/gitpoint/id1251245162?mt=8\">\n", Html.FROM_HTML_MODE_COMPACT));
        } else {
            readmeTextView.setText(Html.fromHtml("<h2>Title</h2><br><p>Description here</p>"));
        }
        upvoteTextView = findViewById(R.id.repodetail_upvotes_txt);
        commentTextView = findViewById(R.id.repodetail_comment_txt);
        starTextView = findViewById(R.id.repodetail_star_txt);
        forkTextView = findViewById(R.id.repodetail_fork_txt);

        upvoteBtn = findViewById(R.id.repodetail_upvote_btn);
        downvoteBtn = findViewById(R.id.repodetail_downvote_btn);
        commentBtn = findViewById(R.id.repodetail_comment_btn);
        starBtn = findViewById(R.id.repodetail_star_btn);
        forkBtn = findViewById(R.id.repodetail_fork_btn);


        viewModel = new ViewModelProvider(this).get(RepoDetailViewModel.class);
        viewModel.didVote = data.valRated;
        viewModel.didStar = data.isStarred;
        viewModel.didFork = data.isForked;
        viewModel.didComment = data.isCommented;

        // Set the observer for the mutable UI elements
        final Observer<Integer> upvoteObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                upvoteTextView.setText(integer.toString());
                upvoteBtn.setImageTintList(viewModel.didVote == 1 ? ColorStateList.valueOf(colorUpvoteSelected) : ColorStateList.valueOf(colorUnselected));
                downvoteBtn.setImageTintList(viewModel.didVote == -1 ? ColorStateList.valueOf(colorDownvoteSelected) : ColorStateList.valueOf(colorUnselected));
            }
        };
        final Observer<Integer> commentObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                commentTextView.setText(integer.toString());
            }
        };
        final Observer<Integer> starObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                starTextView.setText(integer.toString());
                starBtn.setImageTintList(viewModel.didStar ? ColorStateList.valueOf(colorStarSelected) : ColorStateList.valueOf(colorUnselected));
            }
        };
        final Observer<Integer> forkObserver = new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                forkTextView.setText(integer.toString());
                forkBtn.setImageTintList(viewModel.didFork ? ColorStateList.valueOf(colorForkSelected) : ColorStateList.valueOf(colorUnselected));
            }
        };

        // Observer the mutable UI elements
        viewModel.getUpvotes().observe(this, upvoteObserver);
        viewModel.getComments().observe(this, commentObserver);
        viewModel.getStars().observe(this, starObserver);
        viewModel.getForks().observe(this, forkObserver);

        viewModel.getUpvotes().setValue(data.rating);
        viewModel.getComments().setValue(data.comments);
        viewModel.getStars().setValue(data.stars);
        viewModel.getForks().setValue(data.forks);

        // Set listeners for the buttons
        upvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Possibly needs a null pointer check?
                viewModel.upvote();
            }
        });
        downvoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.downvote();
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When being tapped do nothing right now
            }
        });
        starBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.star();
            }
        });
        forkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.fork();
            }
        });


        // setup toolbar
        Toolbar toolbar = findViewById(R.id.repodetail_toolbar);
        setSupportActionBar(toolbar);
    }


}
