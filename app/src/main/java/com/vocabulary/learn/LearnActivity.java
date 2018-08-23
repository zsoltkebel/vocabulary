package com.vocabulary.learn;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.vocabulary.learn.ActivityLearnConfiguration.CHEATING;
import static com.vocabulary.learn.ActivityLearnConfiguration.PREFS_LEARN;
import static com.vocabulary.learn.ActivityLearnConfiguration.SHOWS;
import static com.vocabulary.learn.ActivityLearnConfiguration.SHOWS_MEANING;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_10_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_15_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_5_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_NO_LIMIT;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_TO_ANSWER;

/**
 * Created by Kébel Zsolt on 1/23/2017.
 */

public class LearnActivity extends RealmActivity {

    @BindView(R.id.icon)
    protected ImageView mIvIcon;

    @BindView(R.id.txt_language)
    protected TextView mTvTitle;
    @BindView(R.id.txt_num_of_words)
    protected TextView mTvNumOfPhrases;
    @BindView(R.id.word)
    protected TextView mTvShowed;
    @BindView(R.id.tv_answer)
    protected TextView mTvHidden;

    int i;
    int random;
    Random rand = new Random();

    Button btnShow;
    LinearLayout layout;

    int wrongAnswers = 0;

    ProgressBar progressBar;
    ProgressTask timer = null;

    int shows;
    int timeToAnswer;
    boolean cheatingEnabled;

    String showed;
    String hidden;

    private Vocabulary mVocabulary;

    private Phrase mCurrentPhrase;

    private List<Integer> mIndexes = new ArrayList<>();
    private int index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        ButterKnife.bind(this);

        String vocabularyId = getIntent().getStringExtra(Vocabulary.ID);
        mVocabulary = mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst();

        for (int i = 0; i < mVocabulary.getPhrases().size(); i++)
            mIndexes.add(i);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);


        getPrefs();

        btnShow = (Button) findViewById(R.id.btn_show_answer);
        layout = (LinearLayout) findViewById(R.id.la);
        mTvHidden.setVisibility(View.GONE);

        generateNextPhrase();

        btnShow.setVisibility(View.VISIBLE);
        numRefresh();
/*
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                words.remove(LearnActivity.this.random);

                if (words.size() == 0) {
                    Intent intent = new Intent(LearnActivity.this, ActivityVocabulary.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("vocabularyCode", vocabularies.get(i).getCode());
                    bundle.putInt("p", i);
                    intent.putExtras(bundle);
                    finish();
                    startActivity(intent);
                } else {
                    tvAnswer.setVisibility(View.GONE);
                    pickNext();
                    tvWord.setText(showed);
                    layout.setVisibility(View.VISIBLE);
                }
                numRefresh();
            }
        });*/


        FrameLayout touchable = (FrameLayout) findViewById(R.id.touchable_layout);

        if (cheatingEnabled)
            touchable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTvShowed.setVisibility(View.GONE);
                    mTvHidden.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mTvHidden.setVisibility(View.GONE);
                    mTvShowed.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

    }

    @OnClick(R.id.btn_next)
    protected void onKnowClicked() {
        //TODO increment phrase's know
        mRealm.beginTransaction();
        mCurrentPhrase.setKnowCount(mCurrentPhrase.getKnowCount() + 1);
        mRealm.commitTransaction();
        mIndexes.remove(index);
        generateNextPhrase();
    }

    @OnClick(R.id.btn_show_answer)
    protected void onDontKnowClicked() {
        mRealm.beginTransaction();
        mCurrentPhrase.setDontKnowCount(mCurrentPhrase.getDontKnowCount() + 1);
        mRealm.commitTransaction();
        generateNextPhrase();
    }

    public void numRefresh() {
        /*
        if (words.size() > 0) {
            TextView tvWordsLeft = (TextView) findViewById(R.id.wordsLeft);
            tvWordsLeft.setText(String.valueOf(words.size() - 1) + " words left");
        }
        */
    }

    private void generateNextPhrase()
    {
        if (mIndexes.size() == 0)
            return;

        if (timer != null)
            timer.cancel(true);

        index = rand.nextInt(mIndexes.size());

        mCurrentPhrase = mVocabulary.getPhrases().get(mIndexes.get(index));

        mTvShowed.setText(mCurrentPhrase.getP1());
        mTvHidden.setText(mCurrentPhrase.getP2());


/*
        random = rand.nextInt(words.size());
        if (shows == SHOWS_MEANING)
        {
            showed = words.get(random).getWord();
            hidden = words.get(random).getWord2();
        }
        else
        {
            showed = words.get(random).getWord2();
            hidden = words.get(random).getWord();
        }
        tvWord.setText(showed);
        tvAnswer.setText(hidden);
*/
        //countDown();
    }

    private void setPhraseTexts(Phrase phrase) {

    }

    public void countDown()
    {
        switch (timeToAnswer)
        {
            case TIME_5_SEC:
                timer = new ProgressTask(5);
                timer.execute();
                break;
            case TIME_10_SEC:
                timer = new ProgressTask(10);
                timer.execute();
                break;
            case TIME_15_SEC:
                timer = new ProgressTask(15);
                timer.execute();
                break;
            default:
                progressBar.setIndeterminate(true);
                break;
        }
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Have you finished learning?");
        dialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (timer != null)
                    timer.cancel(true);
                finish();
            }
        });
        dialogBuilder.setNegativeButton("No", null);
        dialogBuilder.create().show();
    }

    private void getPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(PREFS_LEARN, MODE_PRIVATE);

        shows = prefs.getInt(SHOWS, SHOWS_MEANING);
        timeToAnswer = prefs.getInt(TIME_TO_ANSWER, TIME_NO_LIMIT); //0 is the default value.
        cheatingEnabled = prefs.getBoolean(CHEATING, true);
    }

    private class ProgressTask extends AsyncTask<Void, Void, Void>
    {
        int duration;

        public ProgressTask(int duration)
        {
            this.duration = duration;
        }

        protected void onPreExecute()
        {
            progressBar.setMax(duration * 100);
            progressBar.setProgress(0);
        }

        @Override
        protected Void doInBackground(Void... voids)
        {
            while (!isCancelled() && progressBar.getProgress() < duration * 100)
            {
                progressBar.incrementProgressBy(1);

                try {
                    Thread.sleep(9);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            btnShow.callOnClick();
        }

        protected void onCancelled()
        {
            Thread.interrupted();
            super.onCancelled();
        }
    }
}
