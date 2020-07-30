package com.example.githubtrailblazer.ui.feed;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.components.repocard.Model;
import com.example.githubtrailblazer.components.repocard.RepoCard;
import com.example.githubtrailblazer.data.RepoCardData;

import java.util.ArrayList;

// TODO: reuse this adapter in the issues feed (ie. add a new type of view holder for issues)

/**
 * RepoCardAdapter class
 */
public class RepoCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_CARD = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private ArrayList<Model> models = new ArrayList<>();
    private boolean loading = false;
    private boolean hasNextPage = true;

    /**
     * RepoCardAdapter.CardViewHolder class
     */
    public static class CardViewHolder extends RecyclerView.ViewHolder {
        public RepoCard card;

        /**
         * Card view holder constructor
         * @param card - the card view
         */
        public CardViewHolder(View card) {
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
    public RepoCardAdapter(RepoCardData[] data) {
        for (RepoCardData d : data) models.add(new Model(d));
    }

    /**
     * Determines the type of view
     * @param position - the view position
     * @return the view type
     */
    @Override
    public int getItemViewType(int position) {
        return (models.get(position) == null) ? VIEW_TYPE_LOADING : VIEW_TYPE_CARD;
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
        if (viewType == VIEW_TYPE_CARD) {
            return new CardViewHolder(View.inflate(context, R.layout.repo_card, null));
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
        if (holder instanceof CardViewHolder) {
            ((CardViewHolder)holder).card.bindModel(models.get(position));
        }
    }

    /**
     * Get the size of the displayed data set
     * @return the data set size
     */
    @Override
    public int getItemCount() {
        return models.size();
    }

    /**
     * Initialize the process of loading more data (append infinite scroll spinner)
     * @param activity - the parent activity
     */
    public void loadMore(Activity activity) {
        if (!loading && hasNextPage) {
            models.add(null);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyItemInserted(models.size() - 1);
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
            models.clear();
            models.add(null);
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
    public void finishLoading(Activity activity, RepoCardData[] data, boolean hasNextPage) {
        if (loading) {
            int start = models.size() - 1;
            models.remove(start);
            for (RepoCardData d : data) models.add(new Model(d));
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    notifyItemRangeChanged(start, Math.max(data.length, 1));
                }
            });
            loading = false;
            this.hasNextPage = hasNextPage;
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