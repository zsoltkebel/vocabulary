package com.vocabulary.screens.edit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.main.ActivityMain;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Kébel Zsolt on 1/15/2017.
 */

public class EditWord extends RealmActivity {
    @BindView(R.id.editText_word) EditText mEtPhrase;
    @BindView(R.id.editText_meaning) EditText mEtMeaning;

    private Phrase mPhrase;

    public static Intent createIntent(Context context, Phrase phrase) {
        Intent intent = new Intent(context, EditWord.class);

        intent.putExtra(Vocabulary.ID, phrase.getVocabularyId());
        intent.putExtra(Phrase.DATE, phrase.getDate());

        return intent;
    }

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phrase);
        ButterKnife.bind(this);

        String vocabularyId = getIntent().getStringExtra(Vocabulary.ID);
        String phraseId = getIntent().getStringExtra(Phrase.DATE);

        mPhrase = mRealm.where(Phrase.class).equalTo(Phrase.VOCABULARY_ID, vocabularyId)
                .equalTo(Phrase.DATE, phraseId).findFirst();

        mEtPhrase.setText(mPhrase.getP1());
        mEtMeaning.setText(mPhrase.getP2());


    }

    public void doneEditing(View view) {

        if (!mPhrase.getP1().equals(mEtPhrase.getText().toString()) ||
                !mPhrase.getP2().equals(mEtMeaning.getText().toString())) {
            mRealm.beginTransaction();
            mPhrase.setP1(mEtPhrase.getText().toString());
            mPhrase.setP2(mEtMeaning.getText().toString());
            mRealm.commitTransaction();

            //hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(mEtPhrase.getWindowToken(), 0);

            Toast.makeText(EditWord.this, R.string.edited, Toast.LENGTH_SHORT).show();
        }
        Intent result = new Intent();
        result.putExtra(Phrase.DATE, mPhrase.getDate());
        setResult(ActivityMain.NO_ACTION, result);
        finish();
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
