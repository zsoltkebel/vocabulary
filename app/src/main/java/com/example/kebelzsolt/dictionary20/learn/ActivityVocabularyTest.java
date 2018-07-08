package com.example.kebelzsolt.dictionary20.learn;

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

import com.example.kebelzsolt.dictionary20.Data;
import com.example.kebelzsolt.dictionary20.R;
import com.example.kebelzsolt.dictionary20.Vocabulary;
import com.example.kebelzsolt.dictionary20.Word;

import java.util.ArrayList;
import java.util.Random;

import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.ALL_PHRASES;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.CHEATING;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.NUMBER_OF_PHRASES;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.PREFS_LEARN;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.SHOWS;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.SHOWS_MEANING;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.SHOWS_PHRASE;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.TIME_10_SEC;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.TIME_15_SEC;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.TIME_5_SEC;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.TIME_NO_LIMIT;
import static com.example.kebelzsolt.dictionary20.learn.ActivityLearnConfiguration.TIME_TO_ANSWER;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;

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

    Vocabulary vocabulary;
    ArrayList<Word> words;
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
        vocabulary = Data.vocabularies.get(intent.getExtras().getInt(KEY_INDEX_OF_VOCABULARY));

        icon = (ImageView) findViewById(R.id.icon);
        title = (TextView) findViewById(R.id.txt_language);
        numOfPhrases = (TextView) findViewById(R.id.txt_num_of_words);
        progressBar = (ProgressBar) findViewById(R.id.pb_time);
        listView = (ListView) findViewById(R.id.lv_test);
        btnFinish = (Button) findViewById(R.id.btn_finish);

        getPrefs();

        Random random = new Random();
        words = new ArrayList<>(vocabulary.getWordList());
        while (words.size() > numberOfAsked)
        {
            words.remove(random.nextInt(words.size()));
        }
        ListViewAdapter adapter = new ListViewAdapter(this, words, shows == SHOWS_PHRASE ? false : true);
        listView.setAdapter(adapter);

        setTitle();
        countDown();
    }

    public void finishTest(View view)
    {
        finish();
    }

    private void setTitle()
    {
        icon.setImageDrawable(vocabulary.getIconDrawable(getBaseContext()));
        title.setText(vocabulary.getTitle());
        numOfPhrases.setText(String.valueOf(words.size()));
    }

    private void getPrefs()
    {
        SharedPreferences prefs = getSharedPreferences(PREFS_LEARN, MODE_PRIVATE);

        shows = prefs.getInt(SHOWS, SHOWS_MEANING);
        timeToAnswer = prefs.getInt(TIME_TO_ANSWER, TIME_NO_LIMIT); //0 is the default value.
        cheatingEnabled = prefs.getBoolean(CHEATING, true);
        isCustom = prefs.getBoolean(ALL_PHRASES, true);
        numberOfAsked = prefs.getInt(NUMBER_OF_PHRASES, 15);
        if (!isCustom)
            numberOfAsked = vocabulary.getNumOfWords();

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
