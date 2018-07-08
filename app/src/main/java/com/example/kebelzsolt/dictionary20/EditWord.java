package com.example.kebelzsolt.dictionary20;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Toast;

import com.example.kebelzsolt.dictionary20.main.vocabulary.ActivityVocabulary;
import com.example.kebelzsolt.dictionary20.main.vocabulary.FragmentPhraseList;
import com.xw.repo.XEditText;

import static com.example.kebelzsolt.dictionary20.main.vocabulary.ActivityVocabulary.KEY_WORD_ID;

/**
 * Created by Kébel Zsolt on 1/15/2017.
 */

public class EditWord extends Activity {

    DBHelper mydb = new DBHelper(this);
    XEditText editTextWord;
    XEditText editTextMeaning;
    Word currentWord;
    int i;
    String language;

    DBHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_word);

        database = new DBHelper(getBaseContext());

        editTextWord = (XEditText) findViewById(R.id.editText_word);
        editTextMeaning = (XEditText) findViewById(R.id.editText_meaning);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        int number = extras.getInt(KEY_WORD_ID);
        i = extras.getInt("p");

        language = extras.getString(FragmentPhraseList.VOCABULARY_CODE);

        currentWord = database.getPhrase(language, number);



        editTextWord.setText(currentWord.getWord());
        editTextMeaning.setText(currentWord.getWord2());

    }

    public void doneEditing(View view) {

        if (!currentWord.getWord().equals(editTextWord.getText().toString()) ||
                !currentWord.getWord2().equals(editTextMeaning.getText().toString())) {
            currentWord.setWord(editTextWord.getText().toString());
            currentWord.setWord2(editTextMeaning.getText().toString());

            database.updateWord(language, currentWord);
            Spanned text = Html.fromHtml("<b>Edited</b>");
            Toast.makeText(EditWord.this, text, Toast.LENGTH_SHORT).show();

            FragmentPhraseList.update();
        }
        ActivityVocabulary.selectable = false;
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
