package com.teyouale.smsspamblock.utils;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.IdRes;

public class ButtonsBar {
    private View view;

    public ButtonsBar(View parentView, @IdRes int layoutId) {
        view = parentView.findViewById(layoutId);
        dismiss();
    }

    // Sets button's parameters
    public boolean setButton(@IdRes int buttonId, String title, View.OnClickListener listener) {
        TextView button = view.findViewById(buttonId);
        if (button != null) {
            button.setText(title);
            button.setOnClickListener(listener);
            button.setVisibility(View.VISIBLE);
            return true;
        }

        return false;
    }

    // Returns true if snack bar is shown
    public boolean isShown() {
        return view.getVisibility() == View.VISIBLE;
    }

    // Shows shack bar
    public void show() {
        if (!isShown()) {
            view.setVisibility(View.VISIBLE);
        }
    }

    // Hides shack bar
    public boolean dismiss() {
        if (isShown()) {
            view.setVisibility(View.GONE);
            return true;
        }
        return false;
    }
}
