package com.vocabulary;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Preferences
{
    public static final String PREFS_SORT = "prefsSort";
    public static final String KEY_SORT = "sort";

    public static final int SORT_ABC_PHRASE = 1;
    public static final int SORT_DATE = 0;

    public static int getSortType(Context context)
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SORT, MODE_PRIVATE);
        return prefs.getInt(KEY_SORT, SORT_DATE);
    }

    public static void setSortType(Context context, int sortType)
    {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_SORT, MODE_PRIVATE).edit();
        editor.putInt(KEY_SORT, sortType);
        editor.apply();
    }
}
