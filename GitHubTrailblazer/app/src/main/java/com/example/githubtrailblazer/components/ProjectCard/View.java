package com.example.githubtrailblazer.components.ProjectCard;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.widget.*;
import androidx.core.content.ContextCompat;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.RepoDetailActivity;

import java.io.InputStream;
import java.util.HashMap;

/**
 * View class
 */
public class View extends LinearLayout {
    private final View _this;
    private boolean isFirstLoad = true;
    private final int index;
    private final Model model;
    private final ProjectCard.IOnReadyCB onReadyCB;
    private final int[] ids_txt = new int[] {
            R.id.projectCard__name, R.id.projectCard__lang, R.id.projectCard__desc, R.id.projectCard__rating,
            R.id.projectCard__comments, R.id.projectCard__stars, R.id.projectCard__forks
    };
    private final HashMap<Integer, TextView> refs_txt = new HashMap<>();
    private final int[] ids_btn = new int[] {
            R.id.projectCard__btnUpvote, R.id.projectCard__btnDownvote, R.id.projectCard__btnComment,
            R.id.projectCard__btnStar, R.id.projectCard__btnFork, R.id.projectCard__btnActions
    };
    private final HashMap<Integer, ImageButton> refs_btn = new HashMap<>();
    private final ImageView profilePic;
    private final ImageView languageColor;
    private String profilePicUrl = "";

    // colors
    private int colorUnselected;
    private int colorStarSelected;
    private int colorCommentSelected;
    private int colorUpvoteSelected;
    private int colorDownvoteSelected;
    private int colorForkSelected;

    /**
     * DownloadProfileTask class
     */
    private class DownloadProfileTask extends AsyncTask<String, Void, Bitmap> {

        /**
         * Async task to be performed
         * @param strings - the url strings
         * @return the resulting bitmap
         */
        @Override
        protected Bitmap doInBackground(String... strings) {
            String url = strings[0];
            Bitmap bitmap = null;
            try {
                InputStream in = new java.net.URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        /**
         * OnFinish callback
         * @param result - the resulting bitmap
         */
        protected void onPostExecute(Bitmap result) {
            profilePic.setImageBitmap(result);
            if (isFirstLoad) {
                onReadyCB.exec(_this, index);
                isFirstLoad = false;
            }
        }
    }

    /**
     * Create new view
     * @param context - the context
     * @param index - the index of the view
     * @param model - the model
     * @param onReadyCB - the onReady callback
     */
    View(Context context, int index, Model model, ProjectCard.IOnReadyCB onReadyCB) {
        super(context);
        _this = this;
        this.index = index;
        this.onReadyCB = onReadyCB;
        this.model = model.addView(this);

        // init colors
        colorUnselected = ContextCompat.getColor(context, R.color.secondary6);
        colorStarSelected = ContextCompat.getColor(context, R.color.projectStar);
        colorCommentSelected = ContextCompat.getColor(context, R.color.projectComment);
        colorUpvoteSelected = ContextCompat.getColor(context, R.color.projectUpvote);
        colorDownvoteSelected = ContextCompat.getColor(context, R.color.projectDownvote);
        colorForkSelected = ContextCompat.getColor(context, R.color.projectFork);

        // inflate and set layout
        this.setOrientation(LinearLayout.VERTICAL);
        this.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_card));
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        android.view.View view = li.inflate(R.layout.project_card, this, true);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int margin = (int) context.getResources().getDimension(R.dimen.app_project_margin_sm);
        layoutParams.setMargins(margin, margin, margin, 0);
        int padding = (int) context.getResources().getDimension(R.dimen.app_project_margin_md);
        view.setPadding(padding, padding, padding, padding);
        view.setLayoutParams(layoutParams);

        // store txt element refs
        for (int id : ids_txt) refs_txt.put(id, (TextView)view.findViewById(id));

        // store btn element refs, bind controller
        Controller controller = new Controller(model);
        for (int id : ids_btn) {
            ImageButton btn = view.findViewById(id);
            btn.setOnClickListener(controller);
            refs_btn.put(id, btn);
        }

        // profile photo ref
        profilePic = view.findViewById(R.id.projectCard__profilePic);
        languageColor = view.findViewById(R.id.projectCard__langCirc);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(android.view.View v) {
                Intent myIntent = new Intent(context, RepoDetailActivity.class);
                myIntent.putExtra("data", model.data);
                context.startActivity(myIntent);
            }
        });
    }

    /**
     * Update view
     * @param data - the new view data
     */
    final void update(ProjectCard.Data data) {
        // update text refs
        refs_txt.get(R.id.projectCard__name).setText(data.name);
        refs_txt.get(R.id.projectCard__lang).setText(data.language);
        refs_txt.get(R.id.projectCard__desc).setText(data.description);
        refs_txt.get(R.id.projectCard__rating).setText(data.rating.toString());
        refs_txt.get(R.id.projectCard__comments).setText(data.comments.toString());
        refs_txt.get(R.id.projectCard__stars).setText(data.stars.toString());
        refs_txt.get(R.id.projectCard__forks).setText(data.forks.toString());

        // update btn refs
        refs_btn.get(R.id.projectCard__btnUpvote).setImageTintList(data.valRated == 1 ? ColorStateList.valueOf(colorUpvoteSelected) : ColorStateList.valueOf(colorUnselected));
        refs_btn.get(R.id.projectCard__btnDownvote).setImageTintList(data.valRated == -1 ? ColorStateList.valueOf(colorDownvoteSelected) : ColorStateList.valueOf(colorUnselected));
        refs_btn.get(R.id.projectCard__btnComment).setImageTintList(data.isCommented ? ColorStateList.valueOf(colorCommentSelected) : ColorStateList.valueOf(colorUnselected));
        refs_btn.get(R.id.projectCard__btnStar).setImageTintList(data.isStarred ? ColorStateList.valueOf(colorStarSelected) : ColorStateList.valueOf(colorUnselected));
        refs_btn.get(R.id.projectCard__btnFork).setImageTintList(data.isForked ? ColorStateList.valueOf(colorForkSelected) : ColorStateList.valueOf(colorUnselected));
        languageColor.getBackground().setColorFilter(model.getGitHubColor(data.language), PorterDuff.Mode.SRC_ATOP);

        // onReady callback
        if (!profilePicUrl.equals(data.profilePicUrl)) {
            new DownloadProfileTask().execute(data.profilePicUrl);
            profilePicUrl = data.profilePicUrl;
        } else if (isFirstLoad) {
            onReadyCB.exec(this, index);
            isFirstLoad = false;
        }
    }
}
