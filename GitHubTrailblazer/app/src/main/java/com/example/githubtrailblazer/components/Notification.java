package com.example.githubtrailblazer.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.connector.Connector;
import org.w3c.dom.Text;

public class Notification extends LinearLayout {
    private final Type type;
    private final String username;
    private final String comment;
    private final String repository;

    public Notification(Context context) {
        super(context);
        type = Type.LIKE;
        username = "";
        comment = "";
        repository = "";
    }

    public Notification(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Notification);
        type = Type.get(a.getInteger(R.styleable.Notification_type, 0));
        username = a.getString(R.styleable.Notification_username);
        comment = a.getString(R.styleable.Notification_comment);
        repository = a.getString(R.styleable.Notification_repository);
        a.recycle();
        init(context);
    }

    public Notification(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Notification);
        type = Type.get(a.getInteger(R.styleable.Notification_type, 0));
        username = a.getString(R.styleable.Notification_username);
        comment = a.getString(R.styleable.Notification_comment);
        repository = a.getString(R.styleable.Notification_repository);
        a.recycle();
        init(context);
    }

    public Notification(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Notification);
        type = Type.get(a.getInteger(R.styleable.Notification_type, 0));
        username = a.getString(R.styleable.Notification_username);
        comment = a.getString(R.styleable.Notification_comment);
        repository = a.getString(R.styleable.Notification_repository);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        if (username.isEmpty() || repository.isEmpty()) return;

        //        int margin = (int) context.getResources().getDimension(R.dimen.app_project_margin_sm);
//        layoutParams.setMargins(margin, margin, margin, 0);
//        int padding = (int) context.getResources().getDimension(R.dimen.app_project_margin_md);
//        detailsContainer.setPadding(padding, padding, padding, padding);

        LinearLayout detailsContainer = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        detailsContainer.setLayoutParams(layoutParams);
        detailsContainer.setOrientation(VERTICAL);



        StyleSpan boldSpan = new StyleSpan(android.graphics.Typeface.BOLD);
        ClickableSpan cs = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Log.e("Notification", "Notification link clicked: " + type.name() + " [type], " + username + " [username], " + repository + " [repository], " + comment + " [comment]");
            }

            @Override public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getContext().getColor(R.color.primary1));
            }
        };

        TextView action = new TextView(context);
        action.setLayoutParams(layoutParams);
        action.setTextColor(context.getColor(R.color.secondary1));
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append("@", boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(username, boldSpan, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssb.append(type == Type.LIKE ? " liked " : " commented on ");
        action.setText(ssb, TextView.BufferType.SPANNABLE);
        action.setMaxLines(1);
        action.setEllipsize(TextUtils.TruncateAt.END);
        detailsContainer.addView(action);

        TextView target = new TextView(context);
        ssb = new SpannableStringBuilder();
        ssb.append(repository, cs, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        target.setMovementMethod(LinkMovementMethod.getInstance());
        target.setText(ssb);
        target.setMaxLines(1);
        target.setEllipsize(TextUtils.TruncateAt.END);
        detailsContainer.addView(target);

        if (type == Type.COMMENT && !comment.isEmpty()) {
            float scale = getResources().getDisplayMetrics().density;

            int quotePaddingDp = 3;
            int quotePaddingPx = (int) (quotePaddingDp * scale + 0.5f);

            int topPaddingDp = 6;
            int topPaddingPx = (int) (topPaddingDp * scale + 0.5f);

            int horizontalPaddingDp = 12;
            int horizontalPaddingPx = (int) (horizontalPaddingDp * scale + 0.5f);

            RelativeLayout quoteContainer = new RelativeLayout(context);
            quoteContainer.setLayoutParams(layoutParams);
            quoteContainer.setPadding(topPaddingPx, topPaddingPx, 0, 0);

            TextView quote = new TextView(context);
            quote.setText(comment);
            quote.setMaxLines(2);
            quote.setEllipsize(TextUtils.TruncateAt.END);
            quote.setPadding(horizontalPaddingPx, 0, horizontalPaddingPx, 0);
            quoteContainer.addView(quote);

            ImageView quoteLeft = new ImageView(context);
            RelativeLayout.LayoutParams quoteLLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            quoteLLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, quote.getId());
            quoteLLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            quoteLeft.setLayoutParams(quoteLLayoutParams);
            quoteLeft.setImageResource(R.drawable.quote_left_solid);
            quoteLeft.setPadding(0, quotePaddingPx, 0, 0);
            quoteContainer.addView(quoteLeft);

            ImageView quoteRight = new ImageView(context);
            RelativeLayout.LayoutParams quoteLRayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            quoteLRayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, quote.getId());
            quoteLRayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            quoteRight.setLayoutParams(quoteLRayoutParams);
            quoteRight.setImageResource(R.drawable.quote_right_solid);
            quoteRight.setPadding(0, 0, 0, quotePaddingPx);
            quoteContainer.addView(quoteRight);



/*
            ViewGroup.LayoutParams quoteRLayoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);



            LinearLayout quoteContainer = new LinearLayout(context);
            quoteContainer.setLayoutParams(layoutParams);
            quoteContainer.setOrientation(HORIZONTAL);

            LinearLayout.LayoutParams quoteLRLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            quoteLRLayoutParams.weight = 0;
            LinearLayout.LayoutParams quoteLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            quoteLayoutParams.weight = 1;
            quoteLayoutParams.gravity = Gravity.BOTTOM;

            ImageView quoteLeft = new ImageView(context);
            quoteLeft.setLayoutParams(quoteLRLayoutParams);
            quoteLeft.setImageResource(R.drawable.quote_left_solid);
            quoteContainer.addView(quoteLeft);

            TextView quote = new TextView(context);
            quote.setLayoutParams(quoteLayoutParams);
            quote.setText(comment);
            quote.setMaxLines(2);
            quote.setEllipsize(TextUtils.TruncateAt.END);
            quoteContainer.addView(quote);

            ImageView quoteRight = new ImageView(context);
            quoteRight.setLayoutParams(quoteLRLayoutParams);
            quoteRight.setImageResource(R.drawable.quote_right_solid);
            quoteContainer.addView(quoteRight);
*/
            detailsContainer.addView(quoteContainer);
        }

        this.addView(detailsContainer);
    }

    public enum Type {
        LIKE(0), COMMENT(1);
        int id;

        Type(int id) {
            this.id = id;
        }

        static Type get(int id) {
            for (Type t : values()) if (t.id == id) return t;
            throw new IllegalArgumentException();
        }
    }
}
