package com.vocabulary.screens.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.R;
import com.xw.repo.XEditText;

import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

/**
 * Created by Kébel Zsolt on 1/15/2017.
 */

public class EditWord extends Activity {

    private String vocabularyId;
    private String phraseId;

    XEditText editTextWord;
    XEditText editTextMeaning;

    Realm realm;

    private Phrase phrase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrase);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        vocabularyId = getIntent().getStringExtra(Vocabulary.ID);
        phraseId = getIntent().getStringExtra(Phrase.DATE);

        phrase = realm.where(Phrase.class).equalTo(Phrase.VOCABULARY_ID, vocabularyId)
                .equalTo(Phrase.DATE, phraseId).findFirst();

        editTextWord = (XEditText) findViewById(R.id.editText_word);
        editTextMeaning = (XEditText) findViewById(R.id.editText_meaning);





        editTextWord.setText(phrase.getP1());
        editTextMeaning.setText(phrase.getP2());


    }

    public void doneEditing(View view) {

        if (!phrase.getP1().equals(editTextWord.getText().toString()) ||
                !phrase.getP2().equals(editTextMeaning.getText().toString())) {
            realm.beginTransaction();
            phrase.setP1(editTextWord.getText().toString());
            phrase.setP2(editTextMeaning.getText().toString());
            realm.commitTransaction();

            //hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editTextWord.getWindowToken(), 0);

            Toast.makeText(EditWord.this, R.string.edited, Toast.LENGTH_SHORT).show();
        }
        Intent result = new Intent();
        result.putExtra(Phrase.DATE, phrase.getDate());
        setResult(1, result);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        onBackPressed();
    }
}
