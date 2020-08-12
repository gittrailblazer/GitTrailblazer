package com.git_trailblazer.v1.components.toggle;

import com.git_trailblazer.v1.data.ToggleOptionData;

import java.util.HashMap;

/**
 * Model class
 */
public class Model {
    private Toggle toggle;
    private HashMap<Integer, ToggleOptionData> optionMap = new HashMap<>();
    private ToggleOptionData[] options;
    private ToggleOptionData selected;

    /**
     * Bind view to model
     * @param toggle - the view
     * @return this instance
     */
    Model bindView(Toggle toggle) {
        this.toggle = toggle;
        return this;
    }

    /**
     * Set supported options
     * @param selected - the selected option
     * @param options - all options (includes selected option)
     * @return this instance
     */
    Model setOptions(ToggleOptionData selected, ToggleOptionData[] options) {
        this.selected = selected;
        this.options = options;
        optionMap.clear();
        for (int i = 0; i <  options.length; ++i) {
            options[i].id = i;
            optionMap.put(options[i].id, options[i]);
        }
        toggle.update(selected);
        return this;
    }

    /**
     * Show options
     * @return this instance
     */
    Model showOptions() {
        toggle.showOptions(selected, options);
        return this;
    }

    /**
     * Select an option from the options list
     * @param id - the option id
     * @return this instance
     */
    Model select(int id) {
        selected = optionMap.get(id);
        toggle.update(selected);
        return this;
    }
}
