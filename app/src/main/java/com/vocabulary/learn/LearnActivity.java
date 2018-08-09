package com.vocabulary.learn;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.Word;
import com.vocabulary.screens.vocabulary.ActivityVocabulary;

import java.util.ArrayList;
import java.util.Random;

import static com.vocabulary.Data.vocabularies;
import static com.vocabulary.learn.ActivityLearnConfiguration.CHEATING;
import static com.vocabulary.learn.ActivityLearnConfiguration.PREFS_LEARN;
import static com.vocabulary.learn.ActivityLearnConfiguration.SHOWS;
import static com.vocabulary.learn.ActivityLearnConfiguration.SHOWS_MEANING;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_10_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_15_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_5_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_NO_LIMIT;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_TO_ANSWER;
import static com.vocabulary.screens.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;

/**
 * Created by Kébel Zsolt on 1/23/2017.
 */

public class LearnActivity extends AppCompatActivity {

    int i;
    int random;
    Random rand = new Random();
    private ArrayList<Word> words;

    Button btnShow;
    LinearLayout layout;

    int wrongAnswers = 0;

    TextView tvWord;
    TextView tvAnswer;

    ProgressBar progressBar;
    ProgressTask timer = null;

    int shows;
    int timeToAnswer;
    boolean cheatingEnabled;

    String showed;
    String hidden;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        getPrefs();

        Intent intent = getIntent();
        i = intent.getExtras().getInt(KEY_INDEX_OF_VOCABULARY);

        words =  new ArrayList<>(vocabularies.get(i).getWordList());

        ImageView icon = (ImageView) findViewById(R.id.icon);
        icon.setImageDrawable(vocabularies.get(i).getIconDrawable(getBaseContext()));

        ///header
        final TextView tvLanguage = (TextView) findViewById(R.id.txt_language);
        final TextView tvNumOfWords = (TextView) findViewById(R.id.txt_num_of_words);
        tvLanguage.setText(vocabularies.get(i).getTitle());
        tvNumOfWords.setText(String.valueOf(vocabularies.get(i).getNumOfWords()) + " words");

        final Button btnNext = (Button) findViewById(R.id.btn_next);
        btnShow = (Button) findViewById(R.id.btn_show_answer);
        layout = (LinearLayout) findViewById(R.id.la);
        tvWord = (TextView) findViewById(R.id.word);
        tvAnswer = (TextView) findViewById(R.id.tv_answer);
        tvAnswer.setVisibility(View.GONE);

        pickNext();
        tvWord.setText(showed);

        btnShow.setVisibility(View.VISIBLE);
        numRefresh();

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
        });

        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wrongAnswers++;
                pickNext();

                tvWord.setText(showed);
                numRefresh();


            }
        });

        FrameLayout touchable = (FrameLayout) findViewById(R.id.touchable_layout);

        if (cheatingEnabled)
            touchable.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    tvWord.setVisibility(View.GONE);
                    tvAnswer.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    tvAnswer.setVisibility(View.GONE);
                    tvWord.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });

    }

    public void numRefresh() {
        if (words.size() > 0) {
            TextView tvWordsLeft = (TextView) findViewById(R.id.wordsLeft);
            tvWordsLeft.setText(String.valueOf(words.size() - 1) + " words left");
        }
    }

    private void  pickNext()
    {
        if (timer != null)
            timer.cancel(true);

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

        countDown();
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
