package com.vocabulary.screens.phrase;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vocabulary.realm.Phrase;

import java.util.ArrayList;

import io.realm.RealmResults;

public class PagerAdapter extends FragmentPagerAdapter {


    ArrayList<Fragment> fragments = new ArrayList<>();

    public PagerAdapter (FragmentManager fragmentManager, RealmResults<Phrase> phrases) {
        super(fragmentManager);

        for (Phrase phrase : phrases)
            fragments.add(FragmentPhrase.newInstance(phrase.getVocabularyId(), phrase.getDate()));

    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
}
