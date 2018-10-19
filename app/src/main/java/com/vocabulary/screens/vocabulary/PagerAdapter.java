package com.vocabulary.screens.vocabulary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.vocabulary.realm.RealmFragment;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private RealmFragment[] mFragments;

    PagerAdapter(FragmentManager fm, RealmFragment[] fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public int getCount() {
        return mFragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        if(position < mFragments.length){
            return mFragments[position];
        }
        return null;
    }

}
