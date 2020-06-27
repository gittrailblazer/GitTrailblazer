package com.example.githubtrailblazer;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.githubtrailblazer.components.ProjectCard.ProjectCard;
import com.example.githubtrailblazer.components.ProjectCard.View;
import com.google.gson.Gson;

import java.io.*;

/**
 * FeedActivity class
 */
public class FeedActivity extends AppCompatActivity {
    /**
     * Handle feed activity creation
     *
     * @param savedInstanceState - the saved instance state bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // load mock data from JSON
        final ProjectCard.Data[] mock = (ProjectCard.Data[]) Helpers.fromRawJSON(this, R.raw.feed_activity_mock, ProjectCard.Data[].class);

        // populate feed with mock data, async
        final int[] loaded = {0, mock.length};
        final LinearLayout[] layouts = new LinearLayout[mock.length];
        final LinearLayout root = findViewById(R.id.feed__list);
        for (int i = 0; i < mock.length; i++) {
            new ProjectCard(this, i, mock[i], new ProjectCard.IOnReadyCB() {
                @Override
                public void exec(View view, int index) {
                    layouts[index] = view;
                    if (++loaded[0] >= loaded[1]) {
                        for (LinearLayout l : layouts) {
                            if (l != null) root.addView(l);
                        }
                    }
                }
            });
        }
    }
}
