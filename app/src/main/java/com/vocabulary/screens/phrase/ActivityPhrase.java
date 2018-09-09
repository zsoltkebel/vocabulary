package com.vocabulary.screens.phrase;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;
import com.vocabulary.realm.Vocabulary;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.vocabulary.screens.vocabulary.ActivityVocabulary.PREFS_VOCABULARY;
import static com.vocabulary.screens.vocabulary.FragmentPhrases.PREFS_SORTING_ASCENDING;
import static com.vocabulary.screens.vocabulary.FragmentPhrases.PREFS_SORTING_FIELD;

public class ActivityPhrase extends RealmActivity {

    @BindView(R.id.vp_phrase)
    protected ViewPager mVpPhrase;

    private String idOfVocabulary;
    private String idOfPhrase;

    private RealmResults<Phrase> mPhrases;
    private Phrase phrase;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase);
        ButterKnife.bind(this);

        idOfVocabulary = getIntent().getStringExtra(Vocabulary.ID);
        idOfPhrase = getIntent().getStringExtra(Phrase.DATE);

        if (getSortingAscending())
            mPhrases = mRealm.where(Phrase.class)
                    .equalTo(Phrase.VOCABULARY_ID, idOfVocabulary)
                    .findAll()
                    .sort(getSortingField(), Sort.ASCENDING);
        else
            mPhrases = mRealm.where(Phrase.class)
                    .equalTo(Phrase.VOCABULARY_ID, idOfVocabulary)
                    .findAll()
                    .sort(getSortingField(), Sort.DESCENDING);

        phrase = mRealm.where(Phrase.class)
                .equalTo(Phrase.VOCABULARY_ID, idOfVocabulary)
                .equalTo(Phrase.DATE, idOfPhrase).findFirst();

        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mPhrases);

        mVpPhrase.setAdapter(pagerAdapter);
        mVpPhrase.setCurrentItem(mPhrases.indexOf(phrase));

        mVpPhrase.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                phrase = ((FragmentPhrase) pagerAdapter.getItem(position)).getPhrase();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //registerForContextMenu(findViewById(R.id.imageButton));
        //openContextMenu(findViewById(R.id.imageButton));
    }

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        finish();
    }

    public void showPopup(View view){

        //PopupMenu popup = add_new PopupMenu(ActivityPhrase.this, findViewById(R.id.imageButton));
        //Inflating the Popup using xml file
        //popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        //.setOnMenuItemClickListener(add_new PopupMenu.OnMenuItemClickListener() {
        //    public boolean onMenuItemClick(MenuItem item) {
        //        Toast.makeText(ActivityPhrase.this,"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
        //        return true;
        //    }
        //});

        //popup.show();//showing popup menu
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void setCurrentPhrase(Phrase phrase) {
        this.phrase = phrase;
    }

    public String getSortingField() {
        SharedPreferences prefs = getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getString(PREFS_SORTING_FIELD, Phrase.DATE);
    }

    public boolean getSortingAscending() {
        SharedPreferences prefs = getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getBoolean(PREFS_SORTING_ASCENDING, false);
    }
}

