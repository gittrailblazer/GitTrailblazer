package com.example.githubtrailblazer.components.issuecard;

import com.example.githubtrailblazer.data.IssueCardData;

public class Model {
    private IssueCard view;
    private final IssueCardData data;

    public Model(IssueCardData data) {
        this.data = data;
    }

    Model bindView(IssueCard view) {
        this.view = view;
        return this;
    }
}
