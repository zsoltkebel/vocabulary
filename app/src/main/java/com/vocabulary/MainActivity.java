package com.vocabulary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.vocabulary.screens.main.ActivityMain;

import java.util.Locale;

/**
 * Created by Kébel Zsolt on 1/19/2017.
 */

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_SETTINGS = "settingsData";
    public static final String PREFS_LANGUAGE = "language";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);
        int language = prefs.getInt(PREFS_LANGUAGE, 0);

        switch (language)
        {
            case 0:
                setLocale("en");
                break;
            case 1:
                setLocale("hu");
                break;
        }

        finish();
    }
    Locale myLocale;
    public void setLocale(String lang)
    {
        if (lang == null)
        {
            myLocale = Locale.getDefault();
        }
        else
        {
            myLocale = new Locale(lang);
        }
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = myLocale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        Intent refresh = new Intent(this, ActivityMain.class);
        startActivity(refresh);
    }
}
