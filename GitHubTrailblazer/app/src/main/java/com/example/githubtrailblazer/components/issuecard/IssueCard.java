package com.example.githubtrailblazer.components.issuecard;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import androidx.annotation.Nullable;

public class IssueCard extends LinearLayout {
    private Controller controller;

    public IssueCard(Context context) {
        super(context);
    }

    public IssueCard(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IssueCard(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IssueCard(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public IssueCard bindModel(Model model) {
        controller = new Controller(model.bindView(this));
        setOnClickListener(controller);
        return this;
    }
}
