package com.vocabulary.screens.more;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.NEW.Vocabulary;
import com.vocabulary.R;

import java.text.ParseException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class ActivityMore extends AppCompatActivity {

    @BindView(R.id.tv_language)
            protected TextView languageTextView;
    @BindView(R.id.tv_title)
            protected TextView titleTextView;
    @BindView(R.id.tv_num_of_phrases)
            protected TextView numOfPhrasesTextView;
    @BindView(R.id.tv_date)
            protected TextView dateTextView;
    @BindView(R.id.iv_icon)
            protected ImageView iconImageView;

    Realm realm;
    Vocabulary vocabulary = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        String vocabularyId = getIntent().getStringExtra(Vocabulary.ID);
        vocabulary = realm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst();

        iconImageView.setImageDrawable(vocabulary.getIconDrawable(this));
        languageTextView.setText(vocabulary.getLanguageReference(this));
        titleTextView.setText(vocabulary.getTitle());
        titleTextView.setSelected(true); // to scroll text automatically
        numOfPhrasesTextView.setText(String.valueOf(vocabulary.getPhrases().size()));
        try {
            dateTextView.setText(Vocabulary.getDateFormat().format(Vocabulary.getDateAndTimeFormat().parse(vocabulary.getDateAndTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        finish();
    }

    @OnClick(R.id.lyt_click_surface)
    protected void onChangeTitleClicked() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_dic_name, null);
        final EditText titleEditText = (EditText) dialogView.findViewById(R.id.editText_dic_name);
        titleEditText.setText(vocabulary.getTitle());
        titleEditText.requestFocus();
        titleEditText.selectAll();
        //show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        builder.setView(dialogView);
        builder.setTitle(R.string.title);
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
                        .equalTo(Vocabulary.LANGUAGE, vocabulary.getLanguage())
                        .equalTo(Vocabulary.TITLE, titleEditText.getText().toString())
                        .findFirst() == null) {
                    realm.beginTransaction();
                    String title = titleEditText.getText().toString();
                    vocabulary.setTitle(title);
                    realm.commitTransaction();

                    titleTextView.setText(vocabulary.getTitle());
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);

                    Toast.makeText(ActivityMore.this, R.string.msg_title_change_successful, Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        });
        builder.show();
    }
}
