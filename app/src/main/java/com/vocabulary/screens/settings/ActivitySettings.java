package com.vocabulary.screens.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.vocabulary.R;
import com.vocabulary.screens.main.ActivityTabLayout;

import java.util.Locale;

/**
 * Created by Kébel Zsolt on 2018. 03. 23..
 */

public class ActivitySettings extends AppCompatActivity {

    final static String ENGLISH = "English";
    final static String HUNGARIAN = "Magyar";
    public static final String PREFS_SETTINGS = "settingsData";
    public static final String PREFS_LANGUAGE = "language";
    final public static String PREFS_NAME = "name";
    final public static String PREFS_GENDER = "gender";

    private static final int MALE = 0;
    private static final int FEMALE = 1;

    private EditText editTextName;
    private RadioGroup genderGroup;
    Spinner spinnerLanguage;

    String name;
    int gender;
    int language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_settings);

        editTextName = (EditText) findViewById(R.id.edittext_name);
        genderGroup = (RadioGroup) findViewById(R.id.radio_gender);
        genderGroup.getChildAt(0).setId(MALE);
        genderGroup.getChildAt(1).setId(FEMALE);
        //get the spinner from the xml.
        spinnerLanguage = (Spinner) findViewById(R.id.spinner_language);
//create a list of items for the spinner.
        String[] items = new String[]{ENGLISH, HUNGARIAN};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        spinnerLanguage.setAdapter(adapter);

        getPrefs();

        setStatusBar();
    }

    public void save(View view)
    {
        setPrefs();

        switch (language)
        {
            case 0:
                setLocale(null);
                break;
            case 1:
                setLocale("hu");
                break;
        }
    }

    public void back(View view)
    {
        finish();
    }

    public void openAbout(View view)
    {
        Intent intent = new Intent(this, ActivityAbout.class);
        startActivity(intent);
    }

    private void getPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        name = prefs.getString(PREFS_NAME, "'a hard-working student'");//"No name defined" is the default value.
        gender = prefs.getInt(PREFS_GENDER, MALE); //0 is the default value.
        language = prefs.getInt(PREFS_LANGUAGE, 0); //0 is the default value.

        editTextName.setText(name);
        editTextName.clearFocus();

        genderGroup.clearCheck();
        ((RadioButton) genderGroup.getChildAt(gender)).setChecked(true);

        spinnerLanguage.setSelection(language);

    }

    private void setPrefs()
    {
        name = editTextName.getText().toString();
        gender =  genderGroup.getCheckedRadioButtonId();
        language = spinnerLanguage.getSelectedItemPosition();

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE).edit();
        editor.putString(PREFS_NAME, name);
        editor.putInt(PREFS_GENDER, gender);
        editor.putInt(PREFS_LANGUAGE, language);
        editor.apply();

        editTextName.clearFocus();
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
        Intent refresh = new Intent(this, ActivityTabLayout.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //ActivityTabLayout.finishActivity();

        startActivity(refresh);
        finish();
    }

    public void setStatusBar()
    {
        int vocabularyColor = R.color.dark;
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(getResources().getColor(R.color.darker));
        window.setNavigationBarColor(getResources().getColor(R.color.darker));
    }
}
