package com.git_trailblazer.v1.components.searchbar;

import java.util.HashMap;
import java.util.regex.Pattern;

public class Model {
    Pattern delimiterPattern;
    HashMap<String, Boolean> tagExistanceMap = new HashMap<>();
    private boolean isSearchDirty = true;
    private SearchBarEditText searchEdit;
    private SearchBar searchContainer;
    private SearchBar.IOnTagCheckCB tagCheckCB;

    /**
     * Bind search bar edit text to this model
     * @param searchEdit - the view
     * @return this instance
     */
    Model setSearchEdit(SearchBarEditText searchEdit) {
        this.searchEdit = searchEdit;
        return this;
    }

    /**
     * Bind search bar container to this
     * @param searchContainer - the view
     * @return this instance
     */
    Model setSearchContainer(SearchBar searchContainer) {
        this.searchContainer = searchContainer;
        return this;
    }

    /**
     * Add a tag
     * @param tag - the tag to be added
     */
    private void addTag(String tag) {
        // ensure tag is non-empty
        if (tag.isEmpty()) return;

        // ensure tag doesn't exist in previously searched tags
        Boolean exists = tagCheckCB.check(tag);
        if (exists != null && exists == true) return;

        // ensure tag doesn't exits in new tags
        exists = tagExistanceMap.get(tag);
        if (exists != null && exists == true) return;

        // tag does not yet exist, add it
        tagExistanceMap.put(tag, true);

        // exec callbacks
        searchContainer.addTag(tag);
    }

    /**
     * Remove a tag
     * @param tag - the tag to be removed
     * @return this instance
     */
    Model removeTag(String tag) {
        tagExistanceMap.remove(tag);
        return this;
    }

    /**
     * Set the tag delimitter
     * @param delimitter - the delimitter string
     * @return this instance
     */
    Model setDelimitter(String delimitter) {
        delimiterPattern = Pattern.compile(delimitter);
        return this;
    }

    /**
     * Parse a character sequence for tags
     * @param s - the character sequence
     * @return this instance
     */
    Model parse(CharSequence s) {
        // search text is not dirty => updated programmatically by us
        if (!isSearchDirty) {
            isSearchDirty = true;
            return this;
        }

        // do nothing if empty
        int seqLen = s.length();
        if (seqLen == 0) return this;

        // split character sequence into tags, determine last tag
        String[] tags = delimiterPattern.split(s);
        int tagEnd = tags.length;
        String lastTag = "";
        if (!delimiterPattern.matcher(s.charAt(seqLen - 1) + "").matches()) {
            tagEnd -= 1;
            lastTag = tags[tags.length - 1];
        }

        // add tags
        for (int i = 0; i < tagEnd; ++i) addTag(tags[i]);

        // update search bar text
        isSearchDirty = false;
        searchEdit.setTo(lastTag);
        return this;
    }

    /**
     * Perform a search
     * @param s - the remaining additional character sequence to be included
     * @return this instance
     */
    Model search(CharSequence s) {
        // add any remaining tags
        addTag(s.toString());
        tagExistanceMap.clear();

        // exec search
        isSearchDirty = false;
        searchEdit.clear();
        searchContainer.search();
        return this;
    }

    /**
     * Set the tag check callback
     * @param callback - the callback
     * @return this instance
     */
    Model setTagCheckCB(SearchBar.IOnTagCheckCB callback) {
        tagCheckCB = callback;
        return this;
    }
}
