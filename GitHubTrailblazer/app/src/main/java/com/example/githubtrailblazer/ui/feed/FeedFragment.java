package com.example.githubtrailblazer.ui.feed;

import android.app.Activity;
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
import android.widget.*;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.components.LoadingSpinner;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.components.ProjectCard.ProjectCard;
import com.example.githubtrailblazer.connector.RepoFeedData;
import com.example.githubtrailblazer.ui.feed.notification.NotificationEntry;
import com.example.githubtrailblazer.ui.feed.notification.NotificationViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Optional;
import java.util.regex.Pattern;

import static android.content.ContentValues.TAG;


/**
 * FeedFragment class
 */
public class FeedFragment extends Fragment {
    private FeedViewModel viewModel = new FeedViewModel();
    private final String separatorPattern = "\\s|,";
    private FeedViewModel.SortByOptions filterSetting = FeedViewModel.SortByOptions.Best_Match;
    boolean isSearchDirty = true;

    // UI element refs
    private EditText searchbar;
    private LinearLayout oldTagContainer;
    private LinearLayout newTagContainer;
    private LinearLayout feed;
    private Spinner feedFilterSpinner;
    private SwipeRefreshLayout swipeToRefresh;
    private LoadingSpinner loading;

    // Firebase ref
    private DatabaseReference mDatabase;
    private DatabaseReference userRef;

    // Recycler refs
    private RecyclerView recyclerView;
    private FirebaseRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        mDatabase = FirebaseDatabase.getInstance().getReference();
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userRef = mDatabase.child("users").child(currentFirebaseUser.getUid());
        fetch();

        // inflate fragment and get context
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        Context context = getActivity();

        // inflate bell
        LinearLayout bellContainer = getActivity().findViewById(R.id.bell_container);
        inflater.inflate(R.layout.bell_layout, bellContainer, true);
        DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
        TextView bell_counter = getActivity().findViewById(R.id.bell_count);

        // Add Firestore sync functions for bell count
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    userRef.child("Notif_count").setValue(0);
                    bell_counter.setVisibility(TextView.INVISIBLE);
                } else {
                    //do something if exists
                    Long bell_count = ((Long) dataSnapshot.child("Notif_count").getValue());
                    bell_counter.setText(Long.toString(bell_count));

                    if (bell_counter.getVisibility() == TextView.INVISIBLE && !bell_counter.getText().equals("0"))
                        bell_counter.setVisibility(TextView.VISIBLE);

                    if (bell_counter.getVisibility() == TextView.VISIBLE && bell_counter.getText().equals("0"))
                        bell_counter.setVisibility(TextView.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w(TAG, "loadPost:onCancelled", error.toException());
            }
        });
        bellContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userRef.child("Notif_count").setValue(0);
                bell_counter.setVisibility(TextView.INVISIBLE);
                drawer.openDrawer(GravityCompat.END);
            }
        });


        // inflate search bar and get search-related refs
        LinearLayout toolbarContainer = getActivity().findViewById(R.id.toolbar_container);
        toolbarContainer.removeAllViews();
        inflater.inflate(R.layout.searchbar, toolbarContainer, true);
        searchbar = toolbarContainer.findViewById(R.id.searchbar);
        newTagContainer = toolbarContainer.findViewById(R.id.newtag_container);
        loading = view.findViewById(R.id.feed__loading);

        // listen to text changes, parse input into tags in real-time
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // auto-generated
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // search text is not dirty => updated programmatically by us
                if (!isSearchDirty) {
                    isSearchDirty = true;
                    return;
                }

                // do nothing if empty
                int seqLen = s.length();
                if (seqLen == 0) return;
                int seqLast = seqLen - 1;

                // split character sequence into tags, determine last tag
                Pattern pattern = Pattern.compile(separatorPattern);
                String[] tags = pattern.split(s);
                int tagEnd = tags.length;
                String lastTag = "";
                if (!pattern.matcher(s.charAt(seqLast) + "").matches()) {
                    tagEnd -= 1;
                    lastTag = tags[tags.length - 1];
                }
                int lastTagLen = lastTag.length();

                // add tags
                for (int i = 0; i < tagEnd; ++i) viewModel.addTag(tags[i]);

                // update search bar text
                isSearchDirty = false;
                int prevSelection = searchbar.getSelectionStart();
                searchbar.setText(lastTag);
                searchbar.setSelection(lastTagLen == seqLen ? prevSelection : lastTagLen);
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
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    // add whatever is in search bar
                    viewModel.addTag(searchbar.getText().toString());

                    // clear search bar
                    isSearchDirty = false;
                    v.setText("");
                    searchbar.clearFocus();

                    // hide keyboard
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    // move tags to container containing previously searched tags
                    while (newTagContainer.getChildCount() > 0) {
                        TextView tagView = (TextView) newTagContainer.getChildAt(0);
                        newTagContainer.removeView(tagView);
                        // update onclick listener
                        tagView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                viewModel.removeTag((TextView)v, false);
                            }
                        });
                        oldTagContainer.addView(tagView);
                    }

                    emptyFeed();
                    viewModel.execQuery();
                    handled = true;
                }
                return handled;
            }
        });

        // initialize the spinner
        feedFilterSpinner = view.findViewById(R.id.feed_filter_spinner);
        assert context != null;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.feed_filter_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        feedFilterSpinner.setAdapter(adapter);

        // handle spinner selects
        feedFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Optional<FeedViewModel.SortByOptions> setting = FeedViewModel.SortByOptions.fromString(parent.getItemAtPosition(pos).toString());
                assert setting.isPresent();
                filterSetting = setting.get();
                if (viewModel.setSortBy(filterSetting)) {
                    emptyFeed();
                    viewModel.execRefresh();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // refs for other UI elements
        oldTagContainer = view.findViewById(R.id.feed__tags);
        feed = view.findViewById(R.id.feed__list);

        // bind to view model
        viewModel
            // bind to tag added events
            .setOnTagAddedCB(new FeedViewModel.ITagAddedCB() {
                @Override
                public void exec(String tag) {
                    // create & add tag
                    TextView tagView = (TextView) getLayoutInflater().inflate(R.layout.tag, null);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, 0, 10, 0);
                    tagView.setLayoutParams(lp);
                    tagView.setText(tag);
                    tagView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            viewModel.removeTag((TextView)v, true);
                        }
                    });
                    newTagContainer.addView(tagView, 0);
                }
            })
            // bind to tag removed events
            .setOnTagRemovedCB(new FeedViewModel.ITagRemovedCB() {
                @Override
                public void exec(TextView tagView, boolean isNewTag) {
                    (isNewTag ? newTagContainer : oldTagContainer).removeView(tagView);
                }
            })
            // bind to query execution
            .setOnQueryResponseCB(new FeedViewModel.IQueryResponseCB() {
                @Override
                public void exec(RepoFeedData data, boolean isNewQuery) {
                    populateFeed(getContext(), data.repositories, data.hasNextPage, isNewQuery);
                }
            });

        // create swipe to refresh
        swipeToRefresh = view.findViewById(R.id.feed_swipeToRefresh);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                emptyFeed();
                viewModel.execRefresh();
                swipeToRefresh.setRefreshing(false);
            }
        });
        swipeToRefresh.setColorSchemeColors(context.getColor(R.color.primary1));

        // load mock data from JSON
        final ProjectCard.Data[] mock = (ProjectCard.Data[]) Helpers.fromRawJSON(context, R.raw.feed_activity_mock, ProjectCard.Data[].class);
        populateFeed(context, mock, true,true);
        loading.start(); // simulate load with mock data -> to be removed once mock data is removed

        return view;
    }

    private void fetch() {
        recyclerView = getActivity().findViewById(R.id.notification_recycler);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        Query query = userRef.child("Notification");

        FirebaseRecyclerOptions<NotificationEntry> options =
                new FirebaseRecyclerOptions.Builder<NotificationEntry>()
                        .setQuery(query, NotificationEntry.class)
                        .build();

        // specify an adapter
        mAdapter = new FirebaseRecyclerAdapter<NotificationEntry, NotificationViewHolder>(options) {

            @Override
            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.notification_row, parent, false);

                return new NotificationViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull NotificationEntry model) {
                holder.setEntryView(model);
            }

        };
        recyclerView.setAdapter(mAdapter);
    }

    /**
     * Empty the feed in preparation to perform a new query
     */
    void emptyFeed() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                feed.removeAllViews();
                loading.setVisibility(View.VISIBLE);
            }
        });
        loading.start();
    }

    /**
     * Populate the feed with data
     */
    void populateFeed(Context context, ProjectCard.Data[] data, boolean hasNextPage, boolean isNewQuery) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // stop loading and exit if nothing to load
                if (data.length == 0) {
                    loading.stop();
                    loading.setVisibility(View.GONE);
                    return;
                }

                // populate feed with data, async
                final int[] loaded = {0, data.length};
                final LinearLayout[] layouts = new LinearLayout[data.length];
                for (int i = 0; i < data.length; i++) {
                    new ProjectCard(context, i, data[i], new ProjectCard.IOnReadyCB() {
                        @Override
                        public void exec(com.example.githubtrailblazer.components.ProjectCard.View view, int index) {
                            layouts[index] = view;
                            if (++loaded[0] >= loaded[1]) {
                                // stop loading
                                loading.stop();
                                // show/hide spinner depending on if more pages are available after
                                loading.setVisibility(hasNextPage ? View.VISIBLE : View.GONE);
                                // add child layouts
                                for (LinearLayout l : layouts) {
                                    if (l != null) feed.addView(l);
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAdapter.startListening();
    }
    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}