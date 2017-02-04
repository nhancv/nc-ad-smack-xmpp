package com.nhancv.hellosmack.helper;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.Normalizer;

/**
 * Created by Nhan Cao on 06-Sep-16.
 */
public class NUtil {


    /**
     * Adjust alpha
     *
     * @param color
     * @param factor
     * @return color was adjusted
     */
    public static int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    /**
     * Show toast
     *
     * @param context
     * @param msg
     */
    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Convert object class to string
     *
     * @param clsObject
     * @return
     */
    public static String toString(Object clsObject) {
        return clsObject.getClass().getName() + ": " + new Gson().toJson(clsObject);
    }

    /**
     * Check is contain text
     *
     * @param search
     * @param originalText
     * @return true if "originalText" contain "search"
     */
    public static boolean isContainText(String search, String originalText) {
        return isContainText(search, originalText, false);
    }

    /**
     * Check is contain text
     *
     * @param search
     * @param originalText
     * @param caseSensitive
     * @return
     */
    public static boolean isContainText(String search, String originalText, boolean caseSensitive) {
        if (search != null && !search.equalsIgnoreCase("")) {
            String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            if (!caseSensitive) normalizedText = normalizedText.toLowerCase();
            int start = normalizedText.indexOf((!caseSensitive) ? search.toLowerCase() : search);
            if (start < 0) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Highlight text
     *
     * @param search
     * @param originalText
     * @return CharSequence had been high lighted
     */
    public static CharSequence highlightText(String search, String originalText) {
        return highlightText(search, originalText, false);
    }

    /**
     * Highlight text
     *
     * @param search
     * @param originalText
     * @param caseSensitive
     * @return
     */
    public static CharSequence highlightText(String search, String originalText, boolean caseSensitive) {
        if (search != null && !search.equalsIgnoreCase("")) {
            String normalizedText = Normalizer.normalize(originalText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            if (!caseSensitive) normalizedText = normalizedText.toLowerCase();
            int start = normalizedText.indexOf((!caseSensitive) ? search.toLowerCase() : search);
            if (start < 0) {
                return originalText;
            } else {
                Spannable highlighted = new SpannableString(originalText);
                while (start >= 0) {
                    int spanStart = Math.min(start, originalText.length());
                    int spanEnd = Math.min(start + search.length(), originalText.length());
                    highlighted.setSpan(new ForegroundColorSpan(Color.parseColor("#821A9D")), spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    start = normalizedText.indexOf(search, spanEnd);
                }
                return highlighted;
            }
        }
        return originalText;
    }

    /**
     * Return relative time
     * @param context
     * @param time
     * @return
     */
    public static String toRelativeTime(Context context, final long time) {
        return DateUtils.getRelativeDateTimeString(context, time, DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL).toString();
    }

}
