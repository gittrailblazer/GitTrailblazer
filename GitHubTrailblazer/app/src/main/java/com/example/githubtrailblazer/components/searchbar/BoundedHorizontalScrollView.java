package com.example.githubtrailblazer.components.searchbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import com.example.githubtrailblazer.R;

/**
 * SearchBarEditText class [used by SearchBar]
 */
public class BoundedHorizontalScrollView extends HorizontalScrollView {
    private final float boundedWidthRatio;

    public BoundedHorizontalScrollView(Context context) {
        super(context);
        boundedWidthRatio = 0;
    }

    public BoundedHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BoundedHorizontalScrollView);
        boundedWidthRatio = a.getFloat(R.styleable.BoundedHorizontalScrollView_bounded_width_ratio, 0);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // limit width to ratio * parent width
        ViewGroup parent = (ViewGroup)this.getParent();
        int newWidth = (int) Math.floor(parent.getMeasuredWidth() * boundedWidthRatio);
        int measuredWidth = MeasureSpec.getSize(widthMeasureSpec);
        if(newWidth > 0 && newWidth < measuredWidth) {
            int measureMode = MeasureSpec.getMode(widthMeasureSpec);
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(newWidth, measureMode);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
