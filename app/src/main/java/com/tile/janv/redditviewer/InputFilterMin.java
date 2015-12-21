package com.tile.janv.redditviewer;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * @see http://stackoverflow.com/questions/14212518/is-there-any-way-to-define-a-min-and-max-value-for-edittext-in-android
 * Created by janv on 21-Dec-15.
 */
public class InputFilterMin implements InputFilter {

    private int min;

    public InputFilterMin(int min) {
        this.min = min;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (input >= min)
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }
}
