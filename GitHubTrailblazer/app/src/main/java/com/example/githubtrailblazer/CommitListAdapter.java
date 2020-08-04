package com.example.githubtrailblazer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import java.util.ArrayList;

/**
 * Custom adapter for displaying contributor data with loading animation
 * Adapted from https://www.youtube.com/watch?v=cKUxiqNB5y0&list=PLgCYzUzKIBE8TUoCyjomGFqzTFcJ05OaC&index=10
 */
public class CommitListAdapter extends ArrayAdapter<Commit> {
    private static final String TAG = "CommitListAdapter";
    private Context mContext;
    private int mResource;
    private int lastPosition = -1;

    /**
     * Holds view objects before attaching them to ListView
     */
    static class ViewHolder {
        TextView contributorName;
        TextView commitDateAndDescription;
        ImageView contributorImage;
    }

    /**
     * Default constructor for the ContributorListAdapter
     * @param context
     * @param resource
     * @param objects
     */
    public CommitListAdapter(Context context, int resource, ArrayList<Commit> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // setup the image loader
        setupImageLoader();

        // create the view result for showing the animation
        final View result;

        // ViewHolder object
        ViewHolder holder;

        // get commit data
        String name = getItem(position).getContributorName();
        String imageURL = getItem(position).getContributorImageURL();
        String commitDateAndDescription = "[" + getItem(position).getCommitDate() + "]\n" + getItem(position).getCommitDescription();


        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(mResource, parent, false);
            holder = new ViewHolder();
            holder.contributorName = (TextView) convertView.findViewById(R.id.text_view_1);
            holder.commitDateAndDescription = (TextView) convertView.findViewById(R.id.text_view_2);
            holder.contributorImage = (ImageView) convertView.findViewById(R.id.image_view_1);

            result = convertView;
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        // handle scrolling animations
        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastPosition = position;

        // images
        ImageLoader imageLoader = ImageLoader.getInstance();
        int defaultImage = mContext.getResources().getIdentifier("@drawable/default_profile", null, mContext.getPackageName());
        DisplayImageOptions options = new DisplayImageOptions
                .Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(defaultImage)
                .showImageOnFail(defaultImage)
                .showImageOnLoading(defaultImage)
                .build();

        // display data
        imageLoader.displayImage(imageURL, holder.contributorImage, options);
        holder.contributorName.setText(name);
        holder.commitDateAndDescription.setText(commitDateAndDescription);
        return convertView;
    }

    /**
     * Setup Universal Image Loader for loading, caching and displaying images
     * Adapted from https://www.stacktips.com/tutorials/android/universal-image-loader-library-in-android
     */
    private void setupImageLoader() {
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions
                .Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(mContext)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache())
                .discCacheSize(100 * 1024 * 1024)
                .build();

        ImageLoader.getInstance().init(config);
    }
}