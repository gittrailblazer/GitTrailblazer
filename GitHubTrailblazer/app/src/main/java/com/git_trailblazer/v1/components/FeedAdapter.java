package com.git_trailblazer.v1.components;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.git_trailblazer.v1.R;
import com.git_trailblazer.v1.components.issuecard.IssueCard;
import com.git_trailblazer.v1.components.repocard.RepoCard;
import com.git_trailblazer.v1.data.IssueCardData;
import com.git_trailblazer.v1.data.RepoCardData;

import java.util.ArrayList;

// TODO: reuse this adapter in the issues feed (ie. add a new type of view holder for issues)

/**
 * RepoCardAdapter class
 */
public class FeedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_REPOCARD = 0;
    private static final int VIEW_TYPE_ISSUECARD = 1;
    private static final int VIEW_TYPE_LOADING = 2;
    private ArrayList<Object> dataset = new ArrayList<>();
    private boolean loading = false;
    private boolean hasNextPage = true;

    /**
     * RepoCardAdapter.CardViewHolder class
     */
    public static class RepoCardViewHolder extends RecyclerView.ViewHolder {
        public RepoCard card;

        /**
         * Card view holder constructor
         * @param card - the card view
         */
        public RepoCardViewHolder(View card) {
            super(card);
            this.card = (RepoCard) card;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = (int) card.getResources().getDimension(R.dimen.app_project_margin_md);
            layoutParams.setMargins(0, 0, 0, margin);
            this.card.setLayoutParams(layoutParams);
        }
    }

    /**
     * RepoCardAdapter.CardViewHolder class
     */
    public static class IssueCardViewHolder extends RecyclerView.ViewHolder {
        public IssueCard card;

        /**
         * Card view holder constructor
         * @param card - the card view
         */
        public IssueCardViewHolder(View card) {
            super(card);
            this.card = (IssueCard) card;
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = (int) card.getResources().getDimension(R.dimen.app_project_margin_md);
            layoutParams.setMargins(0, 0, 0, margin);
            this.card.setLayoutParams(layoutParams);
        }
    }

    /**
     * RepoCardAdapter.LoadingViewHolder class
     */
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public View loading;

        /**
         * Loading view holder constructor
         * @param loading - the loading spinner view
         */
        public LoadingViewHolder(View loading) {
            super(loading);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            loading.setLayoutParams(layoutParams);
            this.loading = loading;
        }
    }

    /**
     * Repo card adapter constructor
     * @param data - the initial data to display
     */
    public FeedAdapter(Object[] data) {
        if (data instanceof RepoCardData[]) {
            for (Object d : data) dataset.add(new com.git_trailblazer.v1.components.repocard.Model((RepoCardData)d));
        } else if (data instanceof IssueCardData[]) {
            for (Object d : data) dataset.add(new com.git_trailblazer.v1.components.issuecard.Model((IssueCardData)d));
        }
    }

    /**
     * Determines the type of view
     * @param position - the view position
     * @return the view type
     */
    @Override
    public int getItemViewType(int position) {
        if (dataset.get(position) == null) return VIEW_TYPE_LOADING;
        else if (dataset.get(position) instanceof com.git_trailblazer.v1.components.repocard.Model) return VIEW_TYPE_REPOCARD;
        else return VIEW_TYPE_ISSUECARD;
    }

    /**
     * Creates new view wrapped in view holder, invoked by the layout manager
     * @param parent - the parent view
     * @param viewType - the view type
     * @return the new view holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == VIEW_TYPE_REPOCARD) {
            return new RepoCardViewHolder(View.inflate(context, R.layout.repo_card, null));
        } else if (viewType == VIEW_TYPE_ISSUECARD) {
            return new IssueCardViewHolder(View.inflate(context, R.layout.issue_card, null));
        } else {
            return new LoadingViewHolder(View.inflate(context, R.layout.loading, null));
        }
    }

    /**
     * Replaces the contents of a view in a view holder, invoked by the layout manager
     * @param holder - the view holder
     * @param position - the position of the view holder
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RepoCardViewHolder) {
            ((RepoCardViewHolder)holder).card.bindModel((com.git_trailblazer.v1.components.repocard.Model) dataset.get(position));
        } else if (holder instanceof IssueCardViewHolder) {
            ((IssueCardViewHolder)holder).card.bindModel((com.git_trailblazer.v1.components.issuecard.Model) dataset.get(position));
        }
    }

    /**
     * Get the size of the displayed data set
     * @return the data set size
     */
    @Override
    public int getItemCount() {
        return dataset.size();
    }

    /**
     * Initialize the process of loading more data (append infinite scroll spinner)
     * @param activity - the parent activity
     */
    public void loadMore(Activity activity) {
        if (!loading && hasNextPage) {
            dataset.add(null);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(dataset.size() - 1);
                }
            });
            loading = true;
        }
    }

    /**
     * Initialize the process of loading new data (empty data set, append infinite scroll spinner)
     * @param activity - the parent activity
     */
    public void loadNew(Activity activity) {
        if (!loading) {
            dataset.clear();
            dataset.add(null);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyDataSetChanged();
                }
            });
            loading = true;
            hasNextPage = true;
        }
    }

    /**
     * Finish the loading process (add the data to the data set, remove infinite scroll spinner)
     * @param activity - the parent activity
     * @param data - the new data
     * @param hasNextPage - does a next page of data exist?
     */
    public void finishLoading(Activity activity, Object[] data, boolean hasNextPage) {
        if (loading) {
            loading = false;
            this.hasNextPage = hasNextPage;
            int start = dataset.size() - 1;
            dataset.remove(start);
            if (data instanceof RepoCardData[]) {
                for (Object d : data) dataset.add(new com.git_trailblazer.v1.components.repocard.Model((RepoCardData)d));
            } else if (data instanceof IssueCardData[]) {
                for (Object d : data) dataset.add(new com.git_trailblazer.v1.components.issuecard.Model((IssueCardData)d));
            } else {
                return;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeChanged(start, Math.max(data.length, 1));
                }
            });
        }
    }

    /**
     * Is the data set currently loading
     * @return is it loading
     */
    public boolean isLoading() {
        return loading;
    }
}