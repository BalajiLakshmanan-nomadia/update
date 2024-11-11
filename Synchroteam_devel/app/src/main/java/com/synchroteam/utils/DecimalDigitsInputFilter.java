package com.synchroteam.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;

public class DecimalDigitsInputFilter implements InputFilter {

    Pattern mPattern;

    public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
        mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1)
                + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
    }


    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {

//		Matcher matcher = mPattern.matcher(dest);
//		if (!matcher.matches())
//			return "";
//		return null;
        Matcher matcher = mPattern.matcher(dest);

        boolean allowDot = true;

        if ((!TextUtils.isEmpty(dest))
                && (dest.length() > 0)
                && (dest.toString().contains("."))) {
            allowDot = false;
        }

        if (source.toString().equals(".")) {
            if (!allowDot) {
                return "";
            } else {
                if (!matcher.matches())
                    return "";
            }
        } else {
            if (!matcher.matches())
                return "";
        }

        return null;
    }

}