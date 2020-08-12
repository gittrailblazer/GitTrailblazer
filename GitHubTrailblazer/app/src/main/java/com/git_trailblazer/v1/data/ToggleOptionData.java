package com.git_trailblazer.v1.data;

/**
 * ToggleOptionData class
 * @param <T> - the value type
 */
public class ToggleOptionData<T> {
    public int id = 0;
    public int iconBtnResId;
    public int iconLstResId;
    public String displayStr;
    public T value;

    public ToggleOptionData(String displayStr, T value, int iconBtnResId, int iconLstResId) {
        this.iconBtnResId = iconBtnResId;
        this.iconLstResId = iconLstResId;
        this.displayStr = displayStr;
        this.value = value;
    }
}