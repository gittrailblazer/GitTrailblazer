package com.example.githubtrailblazer.components.toggle;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import com.example.githubtrailblazer.R;
import com.example.githubtrailblazer.data.ToggleOptionData;

import java.util.ArrayList;

/**
 * ToggleClass
 * @param <T> - the toggle option value type
 */
@SuppressLint("AppCompatCustomView")
public class Toggle<T> extends Button {
    private Model model;
    private Controller controller;
    private IOnOptionSelectedCB optionSelectedCB;

    /**
     * Option selected callback interface
     * @param <T> - the option value type
     */
    public interface IOnOptionSelectedCB<T> {
        void handle(T value);
    }

    public Toggle(Context context) {
        super(context);
        init();
    }

    public Toggle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Toggle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Toggle(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    /**
     * Setup toggle
     */
    private void init() {
        this.model = new Model().bindView(this);
        this.controller = new Controller(model);
        this.setOnClickListener(controller);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(layoutParams);
    }

    /**
     * Set toggle options
     * @param options - the list of toggle options
     * @return this instance
     */
    public Toggle setOptions(ToggleOptionData<T>[] options) {
        model.setOptions(options[0], options);
        return this;
    }

    /**
     * Set option selected callback
     * @param callback - the option selected callback
     * @return this instance
     */
    public Toggle setOnOptionSelected(IOnOptionSelectedCB callback) {
        optionSelectedCB = callback;
        return this;
    }

    /**
     * Show options popup menu
     * @param selected - the currently selected option
     * @param options - all options
     */
    void showOptions(ToggleOptionData selected, ToggleOptionData[] options) {
        // create popup
        Context wrapper = new ContextThemeWrapper(getContext(), R.style.PopupMenuStyle);
        PopupMenu popupMenu = new PopupMenu(wrapper, this);
        popupMenu.setOnMenuItemClickListener(controller);
        Menu menu = popupMenu.getMenu();
        for (int i = 0; i < options.length; ++i) {
            ToggleOptionData option = options[i];
            if (option.id != selected.id) {
                Log.e("DEBUG", "added: " + selected.id);
                menu.add(0, option.id, i, option.displayStr);
                menu.findItem(option.id).setIcon(option.iconLstResId);
            }
        }

        // add icons
        MenuPopupHelper menuHelper = new MenuPopupHelper(wrapper, (MenuBuilder) menu, this);
        menuHelper.setForceShowIcon(true);
        menuHelper.show();
    }

    /**
     * Update the currently selected option
     * @param selected
     */
    void update(ToggleOptionData<T> selected) {
        setText(selected.displayStr);
        setCompoundDrawablesWithIntrinsicBounds(selected.iconBtnResId, 0, 0, 0);
        int padding = getResources().getDimensionPixelOffset(R.dimen.app_project_margin_md);
        setCompoundDrawablePadding(padding);
        if (optionSelectedCB != null) optionSelectedCB.handle(selected.value);
    }
}
