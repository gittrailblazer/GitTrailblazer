package com.git_trailblazer.v1.components.searchbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * SearchBarEditText class [used exclusively by SearchBar]
 */
@SuppressLint("AppCompatCustomView")
public class SearchBarEditText extends EditText {

    public SearchBarEditText(Context context) {
        super(context);
    }

    public SearchBarEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchBarEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SearchBarEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Bind to a model
     * @param model - the model
     * @return this instance
     */
    SearchBarEditText bindModel(Model model) {
        model.setSearchEdit(this);
        Controller controller = new Controller(model);
        addTextChangedListener(controller);
        setOnEditorActionListener(controller);
        return this;
    }

    /**
     * Clear the search bar edit text, hide the keyboard, lose focus
     * @return this instance
     */
    SearchBarEditText clear() {
        setText("");
        clearFocus();
        // hide keyboard
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
        return this;
    }

    /**
     * Set the search bar edit text, update cursor position
     * @param text - the text to set to
     * @return this instance
     */
    SearchBarEditText setTo(String text) {
        int prevSelection = getSelectionStart();
        int prevTextLength = getText().length();
        int newTextLength = text.length();
        setText(text);
        setSelection(newTextLength == prevTextLength ? prevSelection : newTextLength);
        return this;
    }

    /**
     * Lose focus on back button press
     * @param keyCode - the keycode
     * @param event - the event
     * @return if the event was handled
     */
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            clearFocus();
        }
        return super.onKeyPreIme(keyCode, event);
    }
}
