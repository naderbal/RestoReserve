package com.example.restoreserve.utils.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.restoreserve.R;


/**
 *
 * <p>
 *     Custom view display a selector UI for input dialog selections.
 * </p>
 */
public class CustomInputSelectorView extends LinearLayout {
    public static final String DEFAULT_VALUE = "Not Set";
    // views
    private TextView tvValue;

    public CustomInputSelectorView(Context context) {
        super(context);
        setupView(context, null);
    }

    public CustomInputSelectorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        String title = getTitle(context, attrs, 0);
        setupView(context, title);
    }

    public CustomInputSelectorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        String title = getTitle(context, attrs, defStyleAttr);
        setupView(context, title);
    }

    /**
     * Returns the title passes in the attrs.
     */
    @Nullable
    private String getTitle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InputSelector, defStyleAttr, 0);
        String title = a.getString(R.styleable.InputSelector_title);
        a.recycle();
        return title;
    }

    /**
     * Inflates the proper layout and initializes the inner views.
     */
    private void setupView(Context context, String title) {
        // inflate layout
        inflate(context, R.layout.view_input_selector, this);
        // get views
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvValue = findViewById(R.id.tvValue);
        // set title
        tvTitle.setText(title != null? title: "");
        tvValue.setText(DEFAULT_VALUE);
    }

    /**
     * Updates value in selector.
     * @param value the new value to be set.
     */
    public void updateValue(@NonNull String value) {
        // set value
        tvValue.setText(value);
    }

    /**
     * Returns value set in selector.
     */
    public String getValue() {
        return tvValue.getText().toString();
    }
}
