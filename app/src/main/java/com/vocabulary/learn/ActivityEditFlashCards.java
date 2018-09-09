package com.vocabulary.learn;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;

import com.vocabulary.R;

public class ActivityEditFlashCards extends AppCompatActivity {

    public final static String PREFS_LEARN = "learnData";
    public final static String SHOWS = "shows";
    public final static String TIME_TO_ANSWER = "timeToAnswer";
    public final static String CHEATING = "cheating";
    public final static String IS_TEST = "isTest";
    public final static String ALL_PHRASES = "allPhrases";
    public final static String NUMBER_OF_PHRASES = "numberOfPhrases";

    public final static int SHOWS_PHRASE = 0;
    public final static int SHOWS_MEANING = 1;

    public final static int TIME_5_SEC = 0;
    public final static int TIME_10_SEC = 1;
    public final static int TIME_15_SEC = 2;
    public final static int TIME_NO_LIMIT = 3;

    RadioGroup rgShows;
    RadioGroup rgTimeToAnswer;
    Switch swCheating;
    Switch swNumberOfPhrases;
    Switch swIsTest;
    EditText editTextNumber;
    LinearLayout customNumber;
    LinearLayout layout;

    Spinner spConfig;
    FrameLayout frame;

    int shows;
    int timeToAnswer;
    boolean cheatingEnabled;
    boolean isTest;
    boolean isCustom;
    int numberOfAsked;

    int indexOfVocabulary;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcard_config);

/*
        rgShows = (RadioGroup) findViewById(R.id.rg_shows);
        rgShows.getChildAt(SHOWS_PHRASE).setId(SHOWS_PHRASE);
        rgShows.getChildAt(SHOWS_MEANING).setId(SHOWS_MEANING);
        rgTimeToAnswer = (RadioGroup) findViewById(R.id.rg_time_to_answer);
        rgTimeToAnswer.getChildAt(TIME_5_SEC).setId(TIME_5_SEC);
        rgTimeToAnswer.getChildAt(TIME_10_SEC).setId(TIME_10_SEC);
        rgTimeToAnswer.getChildAt(TIME_15_SEC).setId(TIME_15_SEC);
        rgTimeToAnswer.getChildAt(TIME_NO_LIMIT).setId(TIME_NO_LIMIT);
        swCheating = (Switch) findViewById(R.id.sw_cheating);
        swNumberOfPhrases = (Switch) findViewById(R.id.sw_numberOfPhrases);
        swIsTest = (Switch) findViewById(R.id.sw_isTest);
        editTextNumber = (EditText) findViewById(R.id.editTextNumber);
        customNumber = (LinearLayout) findViewById(R.id.layoutCustomNumber);
        layout  = (LinearLayout) findViewById(R.id.layout_test_settings);






        swCheating.setOnCheckedChangeListener(add_new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    swCheating.setText(R.string.enabled);
                else
                    swCheating.setText(R.string.disabled);
            }
        });
        swNumberOfPhrases.setOnCheckedChangeListener(add_new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    swNumberOfPhrases.setText(R.string.custom);
                    customNumber.setVisibility(View.VISIBLE);
                    editTextNumber.setText(String.valueOf(numberOfAsked));
                }
                else
                {
                    swNumberOfPhrases.setText(R.string.all);
                    customNumber.setVisibility(View.GONE);
                }

            }
        });

        setStatusBar();
        getPrefs();*/
    }
}
