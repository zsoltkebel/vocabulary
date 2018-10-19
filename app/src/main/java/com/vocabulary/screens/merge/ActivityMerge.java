package com.vocabulary.screens.merge;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.more.ActivityMore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityMerge extends RealmActivity {
    @BindView(R.id.iv_icon) ImageView ivIconCurrent;
    @BindView(R.id.iv_icon_2) ImageView ivIconSelected;
    @BindView(R.id.iv_icon_3) ImageView ivIconMerged;
    @BindView(R.id.tv_language) TextView tvLanguageCurrent;
    @BindView(R.id.tv_title) TextView tvTitleCurrent;
    @BindView(R.id.tv_num_of_phrases) TextView tvNumOfPhrasesCurrent;
    @BindView(R.id.tv_language_2) TextView tvLanguageSelected;
    @BindView(R.id.tv_title_2) TextView tvTitleSelected;
    @BindView(R.id.tv_num_of_phrases_2) TextView tvNumOfPhrasesSelected;
    @BindView(R.id.tv_language_3) TextView tvLanguageMerged;
    @BindView(R.id.tv_title_3) TextView tvTitleMerged;
    @BindView(R.id.tv_num_of_phrases_3) TextView tvNumOfPhrasesMerged;

    Vocabulary mVocabularyCurrent;
    Vocabulary mVocabularySelected;

    AlertDialog selectDialog;
    String newName = null;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        ButterKnife.bind(this);

        mVocabularyCurrent = mRealm.where(Vocabulary.class)
                .equalTo(Vocabulary.ID, getIntent().getStringExtra(Vocabulary.ID))
                .findFirst();

        setupCurrent();
        makeDialog();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        onBackPressed();
    }

    @OnClick(R.id.lt_click_select)
    protected void onSelectVocabularyClicked() {

        selectDialog.show();
        selectDialog.getWindow().getDecorView().getBackground().setColorFilter(
                getResources().getColor(R.color.background_light), PorterDuff.Mode.DARKEN);
    }

    @OnClick(R.id.lt_click_rename)
    protected void onRenameClicked() {
        if (mVocabularySelected == null)
            return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
        final EditText titleEditText = (EditText) dialogView.findViewById(R.id.editText_dic_name);
        titleEditText.setText(mVocabularySelected.getTitle());
        titleEditText.requestFocus();
        titleEditText.selectAll();
        //show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        builder.setView(dialogView);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
            }
        });
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mRealm.where(Vocabulary.class)
                        .equalTo(Vocabulary.LANGUAGE, mVocabularySelected.getLanguage())
                        .equalTo(Vocabulary.TITLE, titleEditText.getText().toString())
                        .findFirst() == null) {
                    newName = titleEditText.getText().toString().trim();
                    tvTitleMerged.setText(newName);

                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);

                    Toast.makeText(ActivityMerge.this, R.string.msg_title_change_successful, Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        });
        builder.show();
    }

    @OnClick(R.id.lt_click_merge)
    protected void onMergeClicked() {
        if (mVocabularySelected == null)
            return;

        final String vSelectedId = mVocabularySelected.getId();
        final String vCurrentId = mVocabularyCurrent.getId();

        mRealm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Vocabulary vCurrent = realm.where(Vocabulary.class).equalTo(Vocabulary.ID, vCurrentId).findFirst();
                Vocabulary vSelected = realm.where(Vocabulary.class).equalTo(Vocabulary.ID, vSelectedId).findFirst();

                for (Phrase phrase : vCurrent.getPhrases()) {
                    phrase.setVocabularyId(vSelected.getId());
                    phrase.setLanguage(vSelected.getLanguage());
                    vSelected.getPhrases().add(phrase);
                }

                if (newName != null)
                    vSelected.setTitle(newName);

                vCurrent.deleteFromRealm();
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(ActivityMerge.this, R.string.merged_msg, Toast.LENGTH_SHORT).show();
                setResult(ActivityMore.SUCCESFUL);
                finish();
            }
        });

    }

    private void makeDialog() {
        AlertDialog.Builder selectDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_vocabulary_picker, null);
        selectDialog.setView(view);
        selectDialog.setTitle("Select vocabulary");
        selectDialog.setNegativeButton(R.string.cancel, null);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rv_vocabularies);

        VocabulariesRecyclerViewAdapter recyclerViewAdapter = new VocabulariesRecyclerViewAdapter(this, mRealm);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerViewAdapter);

        this.selectDialog = selectDialog.create();
    }

    private void setupCurrent() {
        ivIconCurrent.setImageDrawable(mVocabularyCurrent.getIconDrawable(this));
        tvLanguageCurrent.setText(mVocabularyCurrent.getLanguageReference(this));
        tvTitleCurrent.setText(mVocabularyCurrent.getTitle());
        tvNumOfPhrasesCurrent.setText(String.valueOf(mVocabularyCurrent.getPhrases().size()));
    }

    public RealmResults<Vocabulary> getVocabularies() {
        return mRealm.where(Vocabulary.class).sort(Vocabulary.SORT_STRING).findAll();
    }

    public Vocabulary getVocabularyCurrent() {
        return mVocabularyCurrent;
    }

    public void setSelectedVocabulary(Vocabulary vocabulary) {
        ivIconSelected.setImageDrawable(vocabulary.getIconDrawable(this));
        tvLanguageSelected.setText(vocabulary.getLanguageReference(this));
        tvTitleSelected.setText(vocabulary.getTitle());
        tvNumOfPhrasesSelected.setText(String.valueOf(vocabulary.getPhrases().size()));

        ivIconMerged.setImageDrawable(vocabulary.getIconDrawable(this));
        tvLanguageMerged.setText(vocabulary.getLanguageReference(this));
        if (tvTitleMerged.getText().toString().equals("...") ||
                mVocabularySelected.getTitle().equals(tvTitleMerged.getText().toString()))
            tvTitleMerged.setText(vocabulary.getTitle());
        tvNumOfPhrasesMerged.setText(String.valueOf(vocabulary.getPhrases().size() + mVocabularyCurrent.getPhrases().size()));

        this.mVocabularySelected = vocabulary;
        selectDialog.dismiss();
    }
}
