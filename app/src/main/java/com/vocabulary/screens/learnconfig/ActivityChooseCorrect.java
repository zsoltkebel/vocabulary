package com.vocabulary.screens.learnconfig;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.FragmentVocabularyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ActivityChooseCorrect extends LearnActivity {
    @BindView(R.id.btn_answer_1)
    protected Button mBtnAnswer1;
    @BindView(R.id.btn_answer_2)
    protected Button mBtnAnswer2;
    @BindView(R.id.btn_answer_3)
    protected Button mBtnAnswer3;
    @BindView(R.id.btn_check)
    Button mBtnCheck;

    @BindView(R.id.flt_vocabulary_info)
    protected FrameLayout mFltVocabularyInfo;

    @BindView(R.id.tv_phrase)
    TextView mTvPhrase;

    private Random mRandom= new Random();

    private FragmentVocabularyInfo fragmentVocabularyInfo;

    private List<Integer> mStateFilters;
    private List<Integer> mIndexes = new ArrayList<>();
    private List<Button> mAnswerButtons = new ArrayList<>();


    private Vocabulary mVocabulary;
    private Phrase mCurrentPhrase;

    private int index;

    private boolean mSelected = false;

    public Intent createIntent() {
        return null;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_choose_correct);
        ButterKnife.bind(this);

        mStateFilters = getIntent().getIntegerArrayListExtra(Phrase.STATE);
        String indexOfVocabulary = getIntent().getStringExtra(Vocabulary.ID);
        mVocabulary = mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, indexOfVocabulary).findFirst();

        mAnswerButtons.add(mBtnAnswer1);
        mAnswerButtons.add(mBtnAnswer2);
        mAnswerButtons.add(mBtnAnswer3);

        for (int i = 0; i < mVocabulary.getPhrases().size(); i++)
            mIndexes.add(i);

        if (mIndexes.size() < 3)
            finish();

        initializeButtons();

        fragmentVocabularyInfo = FragmentVocabularyInfo.newInstance(mVocabulary.getIconInt(this), mVocabulary.getTitle(), mVocabulary.getPhrases().size());
        fragmentVocabularyInfo.displayFragment(this, mFltVocabularyInfo);

        //generateNext();
        Window window = getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.transparent_black));
    }

    @Override
    protected void onStart() {
        generateNext();
        super.onStart();
    }

    private void initializeButtons() {
        mBtnAnswer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAnswer2.setActivated(false);
                mBtnAnswer3.setActivated(false);
                mBtnAnswer1.setActivated(true);
                mSelected = true;
                mBtnCheck.setEnabled(true);
                mBtnCheck.setAlpha(1.0f);
            }
        });
        mBtnAnswer2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAnswer1.setActivated(false);
                mBtnAnswer3.setActivated(false);
                mBtnAnswer2.setActivated(true);
                mSelected = true;
                mBtnCheck.setEnabled(true);
                mBtnCheck.setAlpha(1.0f);
            }
        });
        mBtnAnswer3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnAnswer1.setActivated(false);
                mBtnAnswer2.setActivated(false);
                mBtnAnswer3.setActivated(true);
                mSelected = true;
                mBtnCheck.setEnabled(true);
                mBtnCheck.setAlpha(1.0f);
            }
        });
    }

    private boolean checked = false;

    @OnClick(R.id.btn_check)
    protected void onCheckClicked() {
        if (checked) {
            checked = false;
            mSelected = false;


            for (int i = 0; i < 3; i++) {
                mAnswerButtons.get(i).setAlpha(1.0f);
                mAnswerButtons.get(i).setActivated(false);
                mAnswerButtons.get(i).setSelected(false);
                mAnswerButtons.get(i).setPressed(false);
                mAnswerButtons.get(i).setClickable(true);
                mAnswerButtons.get(i).setBackground(getResources().getDrawable(R.drawable.ripple_container_whit_accent_rounded));

            }
            mBtnCheck.setText("Check");

            generateNext();
        } else if (mSelected) {
            checked = true;

            for (int i = 0; i < 3; i++) {
                mAnswerButtons.get(i).setClickable(false);
                if (mAnswerButtons.get(i).getText().equals(mCorrectMeaning)) {
                    mAnswerButtons.get(i).setActivated(false);
                    mAnswerButtons.get(i).setBackground(getResources().getDrawable(R.color.dot_green));
                    mIndexes.remove(index);
                } else {
                   mAnswerButtons.get(i).setAlpha(0.2f);
                   if (mAnswerButtons.get(i).isActivated()) {
                       mAnswerButtons.get(i).setActivated(false);
                       mAnswerButtons.get(i).setSelected(true);
                   }
                }
            }
            mBtnCheck.setText("Next");
        }
    }

    int indexRight;
    int indexFalse1;
    int indexFalse2;

    String mGivenPhrase;
    String mCorrectMeaning;

    private void generateNext() {

        if (mIndexes.size() == 0) {
            finish();
            return;
        }

        mBtnCheck.setEnabled(false);
        mBtnCheck.setAlpha(0.5f);

        index = mRandom.nextInt(mIndexes.size());

        mCurrentPhrase = mVocabulary.getPhrases().get(mIndexes.get(index));
        if (!mStateFilters.contains(mCurrentPhrase.calculateState())) {
            mIndexes.remove(index);
            generateNext();
            return;
        }

        mGivenPhrase = mCurrentPhrase.getP2();
        mCorrectMeaning = mCurrentPhrase.getP1();

        for (int i = 0; i < 3; i++)
            mAnswerButtons.get(i).setText("");

        mTvPhrase.setText(mGivenPhrase);

        indexRight = mRandom.nextInt(3);
        mAnswerButtons.get(indexRight).setText(mCorrectMeaning);

        indexFalse1 = mRandom.nextInt(mVocabulary.getPhrases().size());
        while (indexFalse1 == mIndexes.get(index))
            indexFalse1 = mRandom.nextInt(mVocabulary.getPhrases().size());
        indexFalse2 = mRandom.nextInt(mVocabulary.getPhrases().size());
        while (indexFalse2 == mIndexes.get(index) || indexFalse2 == indexFalse1)
            indexFalse2 = mRandom.nextInt(mVocabulary.getPhrases().size());

        int arrayIndex = 0;
        String[] falseAnswers = new String[]{
                mVocabulary.getPhrases().get(indexFalse1).getP1(),
                mVocabulary.getPhrases().get(indexFalse2).getP1()
        };

        for (int i = 0; i < 3; i++) {
            if (!mAnswerButtons.get(i).getText().equals(mCorrectMeaning)) {
                mAnswerButtons.get(i).setText(falseAnswers[arrayIndex]);
                arrayIndex++;
            }
        }

        animateAnswersIn();
    }

    int i = 0;
    Button mBtnToAnimate;
    Runnable animate = new Runnable() {
        @Override
        public void run() {
            mBtnToAnimate = mAnswerButtons.get(i);
            mBtnToAnimate.setTranslationX(-mBtnToAnimate.getWidth());
            mBtnToAnimate.setVisibility(View.VISIBLE);
            mBtnToAnimate.animate()
                    .translationXBy(mBtnToAnimate.getWidth())
                    .setDuration(250)
                    .setInterpolator(new AccelerateDecelerateInterpolator());

            if (i < 2) {
                i++;
                new Handler().postDelayed(animate, 200);
            }
        }
    };

    private void animateAnswersIn() {
        this.i = 0;
        for (int i = 0; i < 3; i++)
            mAnswerButtons.get(i).setVisibility(View.INVISIBLE);
        new Handler().post(animate);
    }
}
