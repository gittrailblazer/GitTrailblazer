package com.example.githubtrailblazer.ui.issues;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.example.githubtrailblazer.Helpers;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.components.searchbar.SearchBar;
import com.example.githubtrailblazer.components.toggle.Toggle;
import com.example.githubtrailblazer.connector.IssueFeedData;
import com.example.githubtrailblazer.data.IssueCardData;
import com.example.githubtrailblazer.data.ToggleOptionData;
import com.example.githubtrailblazer.ui.FeedAdapter;


public class IssuesFragment extends Fragment {
    private IssuesViewModel viewModel = new IssuesViewModel();
    private final String delimiterPattern = "\\s|,";

    // feed-specific refs
    private LinearLayout tagContainer;
    private SwipeRefreshLayout swipeToRefresh;
    private FeedAdapter feedAdapter;

    // Firebase-specific refs
//    private DatabaseReference mDatabase;
//    private DatabaseReference userRef;
//    private RecyclerView recyclerView;
//    private FirebaseRecyclerAdapter mAdapter;
//    private RecyclerView.LayoutManager layoutManager;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

//        mDatabase = FirebaseDatabase.getInstance().getReference();
//        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
//        userRef = mDatabase.child("users").child(currentFirebaseUser.getUid());
//        fetch();

        // setup fragment
        Context context = getActivity();
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        tagContainer = view.findViewById(R.id.feed__tags);

        // setup search bar
        LinearLayout toolbarContainer = getActivity().findViewById(R.id.toolbar_container);
        toolbarContainer.removeAllViews();
        inflater.inflate(R.layout.searchbar, toolbarContainer, true);
        ((SearchBar)toolbarContainer.findViewById(R.id.searchbar))
                .initialize(delimiterPattern)
                .setPlaceholder(R.string.issue_searchbar_hint_default)
                .setOnTagCheck(new SearchBar.IOnTagCheckCB() {
                    @Override
                    public Boolean check(String tag) {
                        return viewModel.tagExists(tag);
                    }
                })
                .setOnSearch(new SearchBar.IOnSearchCB() {
                    @Override
                    public void exec(String[] tags) {
                        feedAdapter.loadNew(getActivity());
                        viewModel.addTags(tags);
                        viewModel.execQuery();
                    }
                });

        // setup feed sort
        ((Toggle)view.findViewById(R.id.feed__sort))
                .setOptions(new ToggleOptionData[] {
                        new ToggleOptionData("Newest", IssuesViewModel.SortOption.NEWEST, R.drawable.calendar_solid_optionstoggle_light, R.drawable.calendar_solid_optionstoggle_dark),
                        new ToggleOptionData("Latest", IssuesViewModel.SortOption.MOST_STARS, R.drawable.star_solid_optiontoggle_light, R.drawable.star_solid_optiontoggle_dark),
                        new ToggleOptionData("Most Reactions", IssuesViewModel.SortOption.MOST_FORKS, R.drawable.sitemap_solid_optionstoggle_light, R.drawable.sitemap_solid_optionstoggle_dark)
                })
                .setOnOptionSelected(new Toggle.IOnOptionSelectedCB<IssuesViewModel.SortOption>() {
                    @Override
                    public void handle(IssuesViewModel.SortOption value) {
                        if (viewModel.setSort(value)) {
                            feedAdapter.loadNew(getActivity());
                            viewModel.execQuery();
                        }
                    }
                });

        // setup feed filter
        ((Toggle)view.findViewById(R.id.feed__filter))
                .setOptions(new ToggleOptionData[]{
                        new ToggleOptionData("Explore", IssuesViewModel.FilterOption.EXPLORE, R.drawable.binoculars_solid_optionstoggle_light, R.drawable.binoculars_solid_optionstoggle_dark),
                        new ToggleOptionData("Created", IssuesViewModel.FilterOption.STARRED, R.drawable.star_solid_optiontoggle_light, R.drawable.star_solid_optiontoggle_dark),
                        new ToggleOptionData("Commented", IssuesViewModel.FilterOption.FOLLOWING, R.drawable.user_solid_optionstoggle_light, R.drawable.user_solid_optionstoggle_dark),
                        new ToggleOptionData("Mentioned", IssuesViewModel.FilterOption.CONTRIBUTED, R.drawable.user_friends_solid_optionstoggle_light, R.drawable.user_friends_solid_optionstoggle_dark),
                        new ToggleOptionData("Assigned", IssuesViewModel.FilterOption.CONTRIBUTED, R.drawable.user_friends_solid_optionstoggle_light, R.drawable.user_friends_solid_optionstoggle_dark)
                })
                .setOnOptionSelected(new Toggle.IOnOptionSelectedCB<IssuesViewModel.FilterOption>() {
                    @Override
                    public void handle(IssuesViewModel.FilterOption value) {
                        if (viewModel.setFilter(value)) {
                            feedAdapter.loadNew(getActivity());
                            viewModel.execQuery();
                        }
                    }
                });


        // inflate bell
//        LinearLayout bellContainer = getActivity().findViewById(R.id.bell_container);
//        inflater.inflate(R.layout.bell_layout, bellContainer, true);
//        DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
//        TextView bell_counter = getActivity().findViewById(R.id.bell_count);

        // Add Firestore sync functions for bell count
//        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.getValue() == null) {
//                    userRef.child("Notif_count").setValue(0);
//                    bell_counter.setVisibility(TextView.INVISIBLE);
//                } else {
//                    //do something if exists
//                    Long bell_count = ((Long) dataSnapshot.child("Notif_count").getValue());
//                    bell_counter.setText(Long.toString(bell_count));
//
//                    if (bell_counter.getVisibility() == TextView.INVISIBLE && !bell_counter.getText().equals("0"))
//                        bell_counter.setVisibility(TextView.VISIBLE);
//
//                    if (bell_counter.getVisibility() == TextView.VISIBLE && bell_counter.getText().equals("0"))
//                        bell_counter.setVisibility(TextView.INVISIBLE);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.w(TAG, "loadPost:onCancelled", error.toException());
//            }
//        });
//        bellContainer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                userRef.child("Notif_count").setValue(0);
//                bell_counter.setVisibility(TextView.INVISIBLE);
//                drawer.openDrawer(GravityCompat.END);
//            }
//        });


        // bind to view model
        viewModel
                // bind to tag added events
                .setOnTagAddedCB(new IssuesViewModel.ITagAddedCB() {
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
                                ((LinearLayout)v.getParent()).removeView(v);
                                viewModel.removeTag(((TextView)v).getText().toString());
                                feedAdapter.loadNew(getActivity());
                                viewModel.execQuery();
                            }
                        });
                        tagContainer.addView(tagView, 0);
                    }
                })
                // bind to query execution
                .setOnQueryResponseCB(new IssuesViewModel.IQueryResponseCB() {
                    @Override
                    public void exec(IssueFeedData data) {
                        feedAdapter.finishLoading(getActivity(), data.issues, data.hasNextPage);
                    }
                });


        // setup swipe to refresh
        swipeToRefresh = view.findViewById(R.id.feed_swipeToRefresh);
        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                feedAdapter.loadNew(getActivity());
                viewModel.execQuery();
                swipeToRefresh.setRefreshing(false);
            }
        });
        swipeToRefresh.setColorSchemeColors(context.getColor(R.color.primary1));


        // use mock data on first load
        // TODO: when nothing is searched, recommend repos based on their profile
        final IssueCardData[] mock = (IssueCardData[]) Helpers.fromRawJSON(context, R.raw.issue_feed_mock, IssueCardData[].class);
        feedAdapter = new FeedAdapter(mock);


        // setup repo feed
        RecyclerView feed = view.findViewById(R.id.feed__list);
        feed.setAdapter(feedAdapter);
        feed.setLayoutManager(new LinearLayoutManager(context));
        feed.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager manager = (LinearLayoutManager)recyclerView.getLayoutManager();

                // disable swipe to refresh if not at top of recycler view
                boolean isSwipeToRefreshEnabled = manager.findFirstCompletelyVisibleItemPosition() <= 0;
                swipeToRefresh.setEnabled(isSwipeToRefreshEnabled);
                if (isSwipeToRefreshEnabled && recyclerView.getScrollState() == 1 && swipeToRefresh.isRefreshing()) {
                    recyclerView.stopScroll();
                }

                // load more if reached bottom of feed
                if (manager.findLastCompletelyVisibleItemPosition() >= feedAdapter.getItemCount() - 2) {
                    feedAdapter.loadMore(getActivity());
                    viewModel.loadMore();
                }
            }
        });

        return view;
    }


//    private void fetch() {
//        recyclerView = getActivity().findViewById(R.id.notification_recycler);
//
//        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(recyclerView.getContext());
//        recyclerView.setLayoutManager(layoutManager);
//        Query query = userRef.child("Notification");
//
//        FirebaseRecyclerOptions<NotificationEntry> options =
//                new FirebaseRecyclerOptions.Builder<NotificationEntry>()
//                        .setQuery(query, NotificationEntry.class)
//                        .build();
//
//        // specify an adapter
//        mAdapter = new FirebaseRecyclerAdapter<NotificationEntry, NotificationViewHolder>(options) {
//
//            @Override
//            public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//                // Create a new instance of the ViewHolder, in this case we are using a custom
//                // layout called R.layout.message for each item
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.notification_row, parent, false);
//
//                return new NotificationViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull NotificationViewHolder holder, int position, @NonNull NotificationEntry model) {
//                holder.setEntryView(model);
//            }
//
//        };
//        recyclerView.setAdapter(feedAdapter);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        mAdapter.startListening();
//    }
//    @Override
//    public void onStop() {
//        super.onStop();
//        mAdapter.stopListening();
//    }
}