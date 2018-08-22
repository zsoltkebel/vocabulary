package com.vocabulary.learn;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vocabulary.R;

import java.util.Random;

import static com.vocabulary.learn.ActivityLearnConfiguration.ALL_PHRASES;
import static com.vocabulary.learn.ActivityLearnConfiguration.CHEATING;
import static com.vocabulary.learn.ActivityLearnConfiguration.NUMBER_OF_PHRASES;
import static com.vocabulary.learn.ActivityLearnConfiguration.PREFS_LEARN;
import static com.vocabulary.learn.ActivityLearnConfiguration.SHOWS;
import static com.vocabulary.learn.ActivityLearnConfiguration.SHOWS_MEANING;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_10_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_15_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_5_SEC;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_NO_LIMIT;
import static com.vocabulary.learn.ActivityLearnConfiguration.TIME_TO_ANSWER;

/**
 * Created by Kébel Zsolt on 2018. 03. 24..
 */

public class ActivityVocabularyTest extends AppCompatActivity
{
    ImageView icon;
    TextView title;
    TextView numOfPhrases;
    ProgressBar progressBar;
    ListView listView;
    Button btnFinish;

    int numberOfAsked;

    ProgressTask timer = null;

    int shows;
    int timeToAnswer;
    boolean cheatingEnabled;
    boolean isCustom;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vocabulary_test);

        Intent intent = getIntent();


        icon = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.txt_language);
        numOfPhrases = (TextView) findViewById(R.id.txt_num_of_words);
        progressBar = (ProgressBar) findViewById(R.id.pb_time);
        listView = (ListView) findViewById(R.id.lv_test);
        btnFinish = (Button) findViewById(R.id.btn_finish);

        getPrefs();

        Random random = new Random();


        setTitle();
        countDown();
    }

    public void finishTest(View view)
    {
        finish();
    }

    private void setTitle()
    {
    }

    private void getPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(PREFS_LEARN, MODE_PRIVATE);

        shows = prefs.getInt(SHOWS, SHOWS_MEANING);
        timeToAnswer = prefs.getInt(TIME_TO_ANSWER, TIME_NO_LIMIT); //0 is the default value.
        cheatingEnabled = prefs.getBoolean(CHEATING, true);
        isCustom = prefs.getBoolean(ALL_PHRASES, true);
        numberOfAsked = prefs.getInt(NUMBER_OF_PHRASES, 15);

    }

    public void countDown()
    {
        switch (timeToAnswer)
        {
            case TIME_5_SEC:
                timer = new ProgressTask(5 * numberOfAsked);
                timer.execute();
                break;
            case TIME_10_SEC:
                timer = new ProgressTask(10 * numberOfAsked);
                timer.execute();
                break;
            case TIME_15_SEC:
                timer = new ProgressTask(15 * numberOfAsked);
                timer.execute();
                break;
            default:
                progressBar.setIndeterminate(true);
                break;
        }
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
            btnFinish.callOnClick();
        }

        protected void onCancelled()
        {
            Thread.interrupted();
            super.onCancelled();
        }
    }
}
