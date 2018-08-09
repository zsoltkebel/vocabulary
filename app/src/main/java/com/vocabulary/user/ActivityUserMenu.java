package com.vocabulary.user;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.vocabulary.R;

/**
 * Created by Kébel Zsolt on 2018. 03. 22..
 */

public class ActivityUserMenu extends AppCompatActivity
{
    private EditText editTextName;
    private RadioGroup genderGroup;

    final public static String PREFS = "userData";
    final public static String PREFS_NAME = "name";
    final public static String PREFS_GENDER = "gender";

    final public static int MALE = 0;
    final public static int FEMALE = 1;
    SharedPreferences.Editor editor;

    String name;
    int gender;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_menu);

        editTextName = (EditText) findViewById(R.id.edittext_name);
        genderGroup = (RadioGroup) findViewById(R.id.radio_gender);
        genderGroup.getChildAt(0).setId(MALE);
        genderGroup.getChildAt(1).setId(FEMALE);

        setStatusBar();

        editor = getSharedPreferences(PREFS, MODE_PRIVATE).edit();


        getPrefs();
    }

    private void getPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        name = prefs.getString(PREFS_NAME, "No name defined");//"No name defined" is the default value.
        gender = prefs.getInt(PREFS_GENDER, MALE); //0 is the default value.

        editTextName.setText(name);
        editTextName.clearFocus();

        genderGroup.clearCheck();
        ((RadioButton) genderGroup.getChildAt(gender)).setChecked(true);

    }

    private void setPrefs()
    {
        editor.putString(PREFS_NAME, editTextName.getText().toString());
        editor.putInt(PREFS_GENDER, genderGroup.getCheckedRadioButtonId());
        editor.apply();

        editTextName.clearFocus();
    }

    public void save(View view)
    {
        setPrefs();
        finish();
    }

    public void back(View view)
    {
        finish();
    }

    public void setStatusBar()
    {
        int vocabularyColor = R.color.dark;
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this ,vocabularyColor));
    }
}
