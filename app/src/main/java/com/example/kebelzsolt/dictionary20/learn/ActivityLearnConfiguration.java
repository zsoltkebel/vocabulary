package com.example.kebelzsolt.dictionary20.learn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.example.kebelzsolt.dictionary20.R;

import droidninja.filepicker.fragments.BaseFragment;

import static com.example.kebelzsolt.dictionary20.Data.vocabularies;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;

/**
 * Created by Kébel Zsolt on 2018. 03. 22..
 */

public class ActivityLearnConfiguration extends AppCompatActivity {

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
        setContentView(R.layout.activity_learn_config);

        indexOfVocabulary = getIntent().getExtras().getInt(KEY_INDEX_OF_VOCABULARY);

        setStatusBar();
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
*/

/*
        spConfig = (Spinner) findViewById(R.id.sp_learningMethod);
        frame = (FrameLayout) findViewById(R.id.frame_conf);

        initalizeSpinner();

        FragmentCardTest fragmentAdd = new FragmentCardTest();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_conf, fragmentAdd).commit();
        /*
        customNumber.setVisibility(View.GONE);

        swCheating.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    swCheating.setText(R.string.enabled);
                else
                    swCheating.setText(R.string.disabled);
            }
        });
        swIsTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    swIsTest.setText(R.string.vocabulary_test);
                    layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    swIsTest.setText(R.string.practise);
                    layout.setVisibility(View.INVISIBLE);
                }
            }
        });
        swNumberOfPhrases.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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

    public void startFlashCards(View view)
    {
        Intent intent = new Intent(this, LearnActivity.class);
        intent.putExtra(KEY_INDEX_OF_VOCABULARY, indexOfVocabulary);
        startActivity(intent);
    }

    public void editFlashCards(View view)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("This feature will be available soon");
        dialog.setPositiveButton("ok", null);
        dialog.create().show();
    }
    public void initalizeSpinner()
    {
        final String PRACTISE = "Practise";
        final String VOCATEST = "Vocabulary test";
        final String[] items = new String[]{PRACTISE, VOCATEST};
//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
//set the spinners adapter to the previously created one.
        spConfig.setAdapter(adapter);


        spConfig.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                BaseFragment newFragment;

                switch (items[position])
                {
                    case PRACTISE:
                        newFragment = new FragmentCardTest();
                        break;
                    case VOCATEST:
                        newFragment = new FragmentVocabularyTest();
                        break;
                    default:
                        newFragment = null;
                }
                transaction.replace(R.id.frame_conf, newFragment);
                transaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
/*
        // Create fragment and give it an argument specifying the article it should show
        ArticleFragment newFragment = new ArticleFragment();
        Bundle args = new Bundle();
        args.putInt(ArticleFragment.ARG_POSITION, position);
        newFragment.setArguments(args);



// Replace whatever is in the fragment_container view with this fragment,
// and add the transaction to the back stack so the user can navigate back

        transaction.addToBackStack(null);
*/
// Commit the transaction

    }

    public void goToLearn(View view)
    {
        setPrefs();
        Intent intent;
        if (isTest)
        {
            if ( isCustom && (editTextNumber.getText().toString().equals("") ||
                    Integer.valueOf(editTextNumber.getText().toString()) <= 0 ||
                    Integer.valueOf(editTextNumber.getText().toString()) > vocabularies.get(indexOfVocabulary).getNumOfWords()))
            {
                Toast.makeText(this, "Type in a number", Toast.LENGTH_SHORT).show();
                return;
            }
            setPrefs();
            intent = new Intent(getBaseContext(), ActivityVocabularyTest.class);
        }
        else
        {
            intent = new Intent(getBaseContext(), LearnActivity.class);
        }

        Bundle dataBundle = new Bundle();
        dataBundle.putInt(KEY_INDEX_OF_VOCABULARY, indexOfVocabulary);

        intent.putExtras(dataBundle);
        startActivity(intent);
    }

    public void back(View view)
    {
        finish();
    }

    private void getPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(PREFS_LEARN, MODE_PRIVATE);

        shows = prefs.getInt(SHOWS, SHOWS_MEANING);
        timeToAnswer = prefs.getInt(TIME_TO_ANSWER, TIME_NO_LIMIT); //0 is the default value.
        cheatingEnabled = prefs.getBoolean(CHEATING, true);
        isTest = prefs.getBoolean(IS_TEST, false);
        isCustom = prefs.getBoolean(ALL_PHRASES, false);
        numberOfAsked = prefs.getInt(NUMBER_OF_PHRASES, 15);

        rgShows.clearCheck();
        ((RadioButton) rgShows.getChildAt(shows)).setChecked(true);

        rgTimeToAnswer.clearCheck();
        ((RadioButton) rgTimeToAnswer.getChildAt(timeToAnswer)).setChecked(true);

        swCheating.setChecked(cheatingEnabled);
        swCheating.setText(cheatingEnabled ? R.string.enabled : R.string.disabled);

        swIsTest.setChecked(isTest);
        swIsTest.setText(isTest ? R.string.vocabulary_test : R.string.practise);
        if (isTest)
        {
            layout.setVisibility(View.VISIBLE);
        }
        else
        {
            layout.setVisibility(View.GONE);
        }

        swNumberOfPhrases.setChecked(isCustom);
        swNumberOfPhrases.setText(isCustom ? R.string.custom : R.string.all);
        if (isCustom)
        {
            customNumber.setVisibility(View.VISIBLE);
            editTextNumber.setText(String.valueOf(numberOfAsked));
        }

    }

    public void setPrefs()
    {
        shows = rgShows.getCheckedRadioButtonId();
        timeToAnswer = rgTimeToAnswer.getCheckedRadioButtonId();

        isTest = swIsTest.isChecked();
        isCustom = swNumberOfPhrases.isChecked();

        SharedPreferences.Editor editor = getSharedPreferences(PREFS_LEARN, MODE_PRIVATE).edit();

        editor.putInt(SHOWS, shows);
        editor.putInt(TIME_TO_ANSWER, timeToAnswer);
        editor.putBoolean(CHEATING, swCheating.isChecked());
        editor.putBoolean(IS_TEST, isTest);
        editor.putBoolean(ALL_PHRASES, isCustom);
        if (isCustom && !editTextNumber.getText().toString().equals("") &&
                Integer.valueOf(editTextNumber.getText().toString()) > 0 &&
                Integer.valueOf(editTextNumber.getText().toString()) <= vocabularies.get(indexOfVocabulary).getNumOfWords())
            editor.putInt(NUMBER_OF_PHRASES, Integer.valueOf(editTextNumber.getText().toString()));
        editor.apply();
    }

    public void setStatusBar()
    {
        final Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        Integer color = getResources().getColor(R.color.purple_dark);

        window.setStatusBarColor(color);
        window.setNavigationBarColor(color);
    }
}
