package com.example.githubtrailblazer.providers;

import android.content.Context;

import java.util.HashMap;

public class SearchProvider {
    static final private HashMap<Integer, SearchProvider> providerMap = new HashMap<>();
    static private SearchProvider activeProvider;

    private Integer swipeRefreshResId;
    private Integer loadingSpinnerResId;
    private Integer searchBarResId;
    private String placeholder;
    private String[] suggestions;
    private String delimiterPattern;
    private IKeyboardEventCB onKeyboardOpenCB;
    private IKeyboardEventCB onKeyboardCloseCB;
    private ISearchBuilderCB onSearchBuilderCB;
    private ISearchSuccessCB onSearchSuccessCB;
    private ISearchErrorCB onSearchErrorCB;



    public interface IProviderCB {
        void provide(SearchProvider search);
    }

    public interface IKeyboardEventCB {
        void exec();
    }

    public interface ISearchBuilderCB {
        void build(Object[] args, boolean isNewQuery);
    }

    public interface ISearchSuccessCB {
        void handle(Object data, boolean hasNextPage);
    }

    public interface ISearchErrorCB {
        void error(String error);
    }



    public static void bind(int consumerId, Context ctx, IProviderCB callback) {
        activeProvider = providerMap.get(consumerId);
        // consumer doesn't exist => create + store a new instance, provide a configurable reference, give access
        if (activeProvider == null) {
            activeProvider = new SearchProvider();
            providerMap.put(consumerId, activeProvider);
            callback.provide(activeProvider);
        }
        // consumer already exists => just give access
        else {
            callback.provide(null);
        }
    }


    private SearchProvider() {
        // TODO
    }

    public SearchProvider setSwipeRefreshLayout(int resourceId) {
        this.swipeRefreshResId = resourceId;
        return this;
    }

    public SearchProvider setLoadingSpinner(int resourceId) {
        this.loadingSpinnerResId = resourceId;
        return this;
    }

    public SearchProvider setSearchBar(int resourceId) {
        this.searchBarResId = resourceId;
        return this;
    }

    public SearchProvider setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    public SearchProvider setSuggestions(String[] suggestions) {
        this.suggestions = suggestions;
        return this;
    }

    public SearchProvider setDelimiterPattern(String pattern) {
        delimiterPattern = pattern;
        return this;
    }

    public SearchProvider setOnKeyboardOpen(IKeyboardEventCB callback) {
        onKeyboardOpenCB = callback;
        return this;
    }

    public SearchProvider setOnKeyboardClose(IKeyboardEventCB callback) {
        onKeyboardCloseCB = callback;
        return this;
    }

    public SearchProvider setOnSearch(ISearchBuilderCB callbackSB, ISearchSuccessCB callbackSS, ISearchErrorCB callbackSE) {
        onSearchBuilderCB = callbackSB;
        onSearchSuccessCB = callbackSS;
        onSearchErrorCB = callbackSE;
        return this;
    }
}
