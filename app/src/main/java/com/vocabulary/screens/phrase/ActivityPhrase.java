package com.vocabulary.screens.phrase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class ActivityPhrase extends AppCompatActivity {

    @BindView(R.id.txtvWord)
            protected TextView txtv;
    @BindView(R.id.txtvMeaning)
            protected TextView txtvMeaning;

    private String idOfVocabulary;
    private String idOfPhrase;

    private Realm realm;
    private Phrase phrase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        idOfVocabulary = getIntent().getStringExtra(Vocabulary.ID);
        idOfPhrase = getIntent().getStringExtra(Phrase.DATE);
        phrase = realm.where(Phrase.class)
                .equalTo(Phrase.VOCABULARY_ID, idOfVocabulary)
                .equalTo(Phrase.DATE, idOfPhrase).findFirst();

        txtv.setText(phrase.getP1());
        txtvMeaning.setText(phrase.getP2());
        txtv.setVisibility(View.VISIBLE);

        registerForContextMenu(findViewById(R.id.imageButton));
        //openContextMenu(findViewById(R.id.imageButton));
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

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        finish();
    }

    public void showPopup(View view){

        PopupMenu popup = new PopupMenu(ActivityPhrase.this, findViewById(R.id.imageButton));
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(ActivityPhrase.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        popup.show();//showing popup menu
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

