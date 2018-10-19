package com.vocabulary.screens.learnconfig;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.vocabulary.R;
import com.vocabulary.learn.LearnActivity;
import com.vocabulary.realm.LearnOverview;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.FragmentVocabularyInfo;
import com.vocabulary.screens.learnconfig.chooseCorrect.FragmentConfigChooseCorrect;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vocabulary.screens.main.FragmentVocabularies.KEY_INDEX_OF_VOCABULARY;

/**
 * Created by Kébel Zsolt on 2018. 03. 22..
 */

public class ActivityLearnConfig extends RealmActivity {

    public final static String PREFS_LEARN = "learnData";
    public final static String SHOWS = "shows";
    public final static String TIME_TO_ANSWER = "timeToAnswer";
    public final static String CHEATING = "cheating";
    public final static String IS_TEST = "isTest";
    public final static String ALL_PHRASES = "allPhrases";
    public final static String NUMBER_OF_PHRASES = "numberOfPhrases";

    public final static String STATE_0 = "state0";
    public final static String STATE_1 = "state1";
    public final static String STATE_2 = "state2";
    public final static String STATE_3 = "state3";

    public final static int SHOWS_PHRASE = 0;
    public final static int SHOWS_MEANING = 1;

    public final static int TIME_5_SEC = 0;
    public final static int TIME_10_SEC = 1;
    public final static int TIME_15_SEC = 2;
    public final static int TIME_NO_LIMIT = 3;


    @BindView(R.id.v_state_new)
    protected ImageView mViewStateNew;
    @BindView(R.id.v_state_dont_know)
    protected ImageView mViewStateDontKnow;
    @BindView(R.id.v_state_kinda)
    protected ImageView mViewStateKinda;
    @BindView(R.id.v_state_know)
    protected ImageView mViewKnow;

    @BindView(R.id.flt_settings)
    protected FrameLayout mFltSettings;
    @BindView(R.id.flt_vocabulary_info)
    protected FrameLayout mFltVocabularyInfo;

    @BindView(R.id.s_method)
    protected Spinner mSpinnerMethod;

    RadioGroup rgShows;
    RadioGroup rgTimeToAnswer;
    Switch swCheating;
    Switch swNumberOfPhrases;
    Switch swIsTest;

    Spinner spConfig;

    int shows;
    int timeToAnswer;
    boolean cheatingEnabled;
    boolean mIsP1Asked = true;
    boolean isTest;
    boolean isCustom;
    int numberOfAsked;
    int mP1Asked;

    private String indexOfVocabulary;

    LearnConfigFragment mFragmentLearnConfig;

    FragmentVocabularyInfo fragmentVocabularyInfo;

    Vocabulary mVocabulary;

    final String[] languages = new String[]{"Flashcard", "Choose the correct"};

    private HashSet<Integer> mStateFilters = new HashSet<>();

    public static Intent createIntent(Context context, String indexOfVocabulary) {
        Intent intent = new Intent(context, ActivityLearnConfig.class);
        intent.putExtra(Vocabulary.ID, indexOfVocabulary);

        return intent;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_config);
        ButterKnife.bind(this);

        indexOfVocabulary = getIntent().getStringExtra(Vocabulary.ID);

        mVocabulary = mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, indexOfVocabulary).findFirst();

        if (mVocabulary.getPhrases().size() == 0) {
            Toast.makeText(this, "There isn't any phrase", Toast.LENGTH_SHORT).show();
            finish();
        }

        mFragmentLearnConfig = new FragmentFlashCardConfig();
        displayFragment(mFragmentLearnConfig);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, languages);
//set the spinners adapter to the previously created one.
        mSpinnerMethod.setAdapter(adapter);


        mSpinnerMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        mFragmentLearnConfig = new FragmentFlashCardConfig();
                        break;
                    case 1:
                        mFragmentLearnConfig = new FragmentConfigChooseCorrect();
                        break;
                }
                displayFragment(mFragmentLearnConfig);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //setStatusBar();
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

        FragmentCardTest fragmentAdd = add_new FragmentCardTest();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame_conf, fragmentAdd).commit();
        /*
        customNumber.setVisibility(View.GONE);

        swCheating.setOnCheckedChangeListener(add_new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    swCheating.setText(R.string.enabled);
                else
                    swCheating.setText(R.string.disabled);
            }
        });
        swIsTest.setOnCheckedChangeListener(add_new CompoundButton.OnCheckedChangeListener() {
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

        Vocabulary vocabulary = mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, indexOfVocabulary).findFirst();
        fragmentVocabularyInfo = FragmentVocabularyInfo.newInstance(vocabulary.getIconInt(this), vocabulary.getTitle(), vocabulary.getPhrases().size());
        fragmentVocabularyInfo.displayFragment(this, mFltVocabularyInfo);

        selectStates();
    }

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        finish();
    }

    @OnClick(R.id.lt_click_play)
    protected void onPlayClicked() {
        if (mRealm.where(LearnOverview.class).equalTo(LearnOverview.ID, mVocabulary.getId()).findFirst() == null) {
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(new LearnOverview(mVocabulary.getId()));
            mRealm.commitTransaction();
        }

        saveSelectedStates();
        mFragmentLearnConfig.startTest(this);
        //finish();
    }

    @OnClick(R.id.v_state_new)
    protected void onStateNewClicked() {
        if (mStateFilters.contains(Phrase.NEW)) {
            if (mStateFilters.size() > 1) {
                mStateFilters.remove(Phrase.NEW);
                mViewStateNew.setActivated(true);
            } else
                notifyUserOneMustPicked();
        } else {
            mStateFilters.add(Phrase.NEW);
            mViewStateNew.setActivated(false);
        }
    }

    @OnClick(R.id.v_state_dont_know)
    protected void onStateDontKnowClicked() {
        if (mStateFilters.contains(Phrase.DONT_KNOW)) {
            if (mStateFilters.size() > 1) {
                mStateFilters.remove(Phrase.DONT_KNOW);
                mViewStateDontKnow.setActivated(true);
            } else
                notifyUserOneMustPicked();
        } else {
            mStateFilters.add(Phrase.DONT_KNOW);
            mViewStateDontKnow.setActivated(false);
        }
    }

    @OnClick(R.id.v_state_kinda)
    protected void onStateKindaClicked() {
        if (mStateFilters.contains(Phrase.KINDA)) {
            if (mStateFilters.size() > 1) {
                mStateFilters.remove(Phrase.KINDA);
                mViewStateKinda.setActivated(true);
            } else
                notifyUserOneMustPicked();
        } else {
            mStateFilters.add(Phrase.KINDA);
            mViewStateKinda.setActivated(false);
        }
    }

    @OnClick(R.id.v_state_know)
    protected void onStateKnowClicked() {
        if (mStateFilters.contains(Phrase.KNOW)) {
            if (mStateFilters.size() > 1) {
                mStateFilters.remove(Phrase.KNOW);
                mViewKnow.setActivated(true);
            } else
                notifyUserOneMustPicked();
        } else {
            mStateFilters.add(Phrase.KNOW);
            mViewKnow.setActivated(false);
        }
    }

    public void notifyUserOneMustPicked() {
        Toast.makeText(this, "You must pick one filter", Toast.LENGTH_SHORT).show();
    }

    public Vocabulary getVocabulary() {
        return mVocabulary;
    }

    public String getIndexOfVocabulary() {
        return indexOfVocabulary;
    }

    public boolean getIsP1Asked() {
        return mIsP1Asked;
    }

    public HashSet<Integer> getStates() {
        return mStateFilters;
    }

    private void displayFragment(Fragment fragment) {
        android.support.v4.app.FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(mFltSettings.getId(), fragment);
        t.commit();
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
/*

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
*/
    }

    public void back(View view)
    {
        finish();
    }

    private void selectStates() {
        mViewStateNew.setActivated(true);
        mViewStateDontKnow.setActivated(true);
        mViewStateKinda.setActivated(true);
        mViewKnow.setActivated(true);

        SharedPreferences prefs = getSharedPreferences(PREFS_LEARN, MODE_PRIVATE);

        if (prefs.getBoolean(STATE_0, true))
            onStateNewClicked();
        if (prefs.getBoolean(STATE_1, true))
            onStateDontKnowClicked();
        if (prefs.getBoolean(STATE_2, true))
            onStateKindaClicked();
        if (prefs.getBoolean(STATE_3, false))
            onStateKnowClicked();

        /*
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
*/
    }

    private void saveSelectedStates() {
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_LEARN, MODE_PRIVATE).edit();

        if (mStateFilters.contains(Phrase.NEW))
            editor.putBoolean(STATE_0, true);
        else
            editor.putBoolean(STATE_0, false);

        if (mStateFilters.contains(Phrase.DONT_KNOW))
            editor.putBoolean(STATE_1, true);
        else
            editor.putBoolean(STATE_1, false);

        if (mStateFilters.contains(Phrase.KINDA))
            editor.putBoolean(STATE_2, true);
        else
            editor.putBoolean(STATE_2, false);

        if (mStateFilters.contains(Phrase.KNOW))
            editor.putBoolean(STATE_3, true);
        else
            editor.putBoolean(STATE_3, false);

        editor.apply();
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
        /*
        if (isCustom && !editTextNumber.getText().toString().equals("") &&
                Integer.valueOf(editTextNumber.getText().toString()) > 0 &&
                Integer.valueOf(editTextNumber.getText().toString()) <= vocabularies.get(indexOfVocabulary).getNumOfWords())
            editor.putInt(NUMBER_OF_PHRASES, Integer.valueOf(editTextNumber.getText().toString()));
            */
        editor.apply();
    }
}
