package com.example.githubtrailblazer.ui.feed;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.example.githubtrailblazer.ghapi.GitHubConnector;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.SearchReposQuery;
import com.example.githubtrailblazer.components.ProjectCard.ProjectCard;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * FeedFragment class
 */
public class FeedFragment extends Fragment {
    private final String separatorPattern = "\\s|,";
    private ArrayList<String> tagList = new ArrayList<>();

    // UI element refs
    private LinearLayout searchbarContainer;
    private EditText searchbar;
    private LinearLayout tags;
    private LinearLayout feed;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        // inflate fragment and get context
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        Context context = getActivity();

        // inflate search bar and get search-related refs
        LinearLayout toolbarContainer = getActivity().findViewById(R.id.toolbar_container);
        toolbarContainer.removeAllViews();
        inflater.inflate(R.layout.searchbar, toolbarContainer, true);
        searchbarContainer = toolbarContainer.findViewById(R.id.searchbar_container);
        searchbar = toolbarContainer.findViewById(R.id.searchbar);

        // listen to text changes, parse input into tags in real-time
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // auto-generated
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // todo - parse input, split into tags by whitespace OR commas, add tags to searchbar_container,
                //          update search bar text
            }

            @Override
            public void afterTextChanged(Editable s) {
                // auto-generated
            }
        });

        // handle search enter
        searchbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;

                // handle ENTER key press (for now, we're just perform tag parse once ENTER is pressed)
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    // process text into tags
                    // TODO - make input parsing more robust (ie. no duplicate tags, no empty tags, etc.)
                    Pattern pattern = Pattern.compile(separatorPattern);
                    tagList.addAll(Arrays.asList(pattern.split(v.getText())));

                    // clear search bar
                    v.setText("");
                    searchbar.clearFocus();
                    addSearchTags();
                    tagList.clear();

                    handled = true;
                }
                return handled;
            }
        });

        // refs for other UI elements
        tags = view.findViewById(R.id.feed__tags);
        feed = view.findViewById(R.id.feed__list);

        // load mock data from JSON
        final ProjectCard.Data[] mock = (ProjectCard.Data[]) Helpers.fromRawJSON(context, R.raw.feed_activity_mock, ProjectCard.Data[].class);

        // populate feed with mock data, async
        final int[] loaded = {0, mock.length};
        final LinearLayout[] layouts = new LinearLayout[mock.length];
        for (int i = 0; i < mock.length; i++) {
            new ProjectCard(context, i, mock[i], new ProjectCard.IOnReadyCB() {
                @Override
                public void exec(com.example.githubtrailblazer.components.ProjectCard.View view, int index) {
                    layouts[index] = view;
                    if (++loaded[0] >= loaded[1]) {
                        for (LinearLayout l : layouts) {
                            if (l != null) feed.addView(l);
                        }
                    }
                }
            });
        }

//        // sample GraphQL query from /issues/10
//        queryHelloWorld();

        return view;
    }

    /**
     * Add newly typed search tags above feed
     */
    private void addSearchTags() {
        for (int i = 0; i < tagList.size(); ++i) {
            // create & add tag
            TextView tag = (TextView) getLayoutInflater().inflate(R.layout.tag, null);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(0, 0, 10, 0);
            tag.setLayoutParams(lp);
            tag.setText(tagList.get(i));
            tag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tags.removeView(v);
                }
            });
            tags.addView(tag);
        }
    }

    /**
     * Make a hello world query and log the output.
     */
    private void queryHelloWorld() {
        GitHubConnector.initialize().query(
                SearchReposQuery.builder().searchString("hello world").build())
                .enqueue(new ApolloCall.Callback<SearchReposQuery.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<SearchReposQuery.Data> response) {
                        Log.d("QUERY", "Successful query: " + response.getData());
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {
                        Log.d("QUERY", "Failed query: " + e.getMessage(), e);
                    }
                });
    }
}