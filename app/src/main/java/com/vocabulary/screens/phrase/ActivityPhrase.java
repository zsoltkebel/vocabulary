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

    @BindView(R.id.vp_phrase) ViewPager mVpPhrase;

    private RealmResults<Phrase> mPhrases;
    private Phrase mCurrentPhrase;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrase);
        ButterKnife.bind(this);

        String idOfVocabulary = getIntent().getStringExtra(Vocabulary.ID);
        String idOfPhrase = getIntent().getStringExtra(Phrase.DATE);

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

        mCurrentPhrase = mRealm.where(Phrase.class)
                .equalTo(Phrase.VOCABULARY_ID, idOfVocabulary)
                .equalTo(Phrase.DATE, idOfPhrase).findFirst();

        final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), mPhrases);

        mVpPhrase.setAdapter(pagerAdapter);
        mVpPhrase.setCurrentItem(mPhrases.indexOf(mCurrentPhrase));

        mVpPhrase.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentPhrase = ((FragmentPhrase) pagerAdapter.getItem(position)).getPhrase();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
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

