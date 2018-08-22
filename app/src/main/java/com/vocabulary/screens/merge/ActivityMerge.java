package com.vocabulary.screens.merge;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.R;
import com.vocabulary.screens.more.ActivityMore;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityMerge extends AppCompatActivity {

    @BindView(R.id.iv_icon)
            protected ImageView ivIconCurrent;
    @BindView(R.id.tv_language)
            protected TextView tvLanguageCurrent;
    @BindView(R.id.tv_title)
            protected TextView tvTitleCurrent;
    @BindView(R.id.tv_num_of_phrases)
            protected TextView tvNumOfPhrasesCurrent;
    @BindView(R.id.iv_icon_2)
            protected ImageView ivIconSelected;
    @BindView(R.id.tv_language_2)
            protected TextView tvLanguageSelected;
    @BindView(R.id.tv_title_2)
            protected TextView tvTitleSelected;
    @BindView(R.id.tv_num_of_phrases_2)
            protected TextView tvNumOfPhrasesSelected;
    @BindView(R.id.iv_icon_3)
            protected ImageView ivIconMerged;
    @BindView(R.id.tv_language_3)
            protected TextView tvLanguageMerged;
    @BindView(R.id.tv_title_3)
        protected TextView tvTitleMerged;
    @BindView(R.id.tv_num_of_phrases_3)
        protected TextView tvNumOfPhrasesMerged;

    Vocabulary vocabularyCurrent;
    Vocabulary vocabularySelected;

    AlertDialog selectDialog;
    String newName = null;

    Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        vocabularyCurrent = realm.where(Vocabulary.class)
                .equalTo(Vocabulary.ID, getIntent().getStringExtra(Vocabulary.ID))
                .findFirst();

        setupCurrent();
        makeDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onStop() {
        super.onStop();
        realm.close();
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
        if (vocabularySelected == null)
            return;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
        final EditText titleEditText = (EditText) dialogView.findViewById(R.id.editText_dic_name);
        titleEditText.setText(vocabularySelected.getTitle());
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
                if (realm.where(Vocabulary.class)
                        .equalTo(Vocabulary.LANGUAGE, vocabularySelected.getLanguage())
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
        if (vocabularySelected == null)
            return;

        final String vSelectedId = vocabularySelected.getId();
        final String vCurrentId = vocabularyCurrent.getId();

        realm.executeTransactionAsync(new Realm.Transaction() {
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

        VocabulariesRecyclerViewAdapter recyclerViewAdapter = new VocabulariesRecyclerViewAdapter(this, realm);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerViewAdapter);

        this.selectDialog = selectDialog.create();
    }

    private void setupCurrent() {
        ivIconCurrent.setImageDrawable(vocabularyCurrent.getIconDrawable(this));
        tvLanguageCurrent.setText(vocabularyCurrent.getLanguageReference(this));
        tvTitleCurrent.setText(vocabularyCurrent.getTitle());
        tvNumOfPhrasesCurrent.setText(String.valueOf(vocabularyCurrent.getPhrases().size()));
    }

    public RealmResults<Vocabulary> getVocabularies() {
        return realm.where(Vocabulary.class).sort(Vocabulary.SORT_STRING).findAll();
    }

    public Vocabulary getVocabularyCurrent() {
        return vocabularyCurrent;
    }

    public void setSelectedVocabulary(Vocabulary vocabulary) {
        ivIconSelected.setImageDrawable(vocabulary.getIconDrawable(this));
        tvLanguageSelected.setText(vocabulary.getLanguageReference(this));
        tvTitleSelected.setText(vocabulary.getTitle());
        tvNumOfPhrasesSelected.setText(String.valueOf(vocabulary.getPhrases().size()));

        ivIconMerged.setImageDrawable(vocabulary.getIconDrawable(this));
        tvLanguageMerged.setText(vocabulary.getLanguageReference(this));
        if (tvTitleMerged.getText().toString().equals("...") ||
                vocabularySelected.getTitle().equals(tvTitleMerged.getText().toString()))
            tvTitleMerged.setText(vocabulary.getTitle());
        tvNumOfPhrasesMerged.setText(String.valueOf(vocabulary.getPhrases().size() + vocabularyCurrent.getPhrases().size()));

        this.vocabularySelected = vocabulary;
        selectDialog.dismiss();
    }
}
