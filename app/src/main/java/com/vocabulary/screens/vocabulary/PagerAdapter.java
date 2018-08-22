package com.vocabulary.screens.vocabulary;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments = new Fragment[]{
            new FragmentPhrases(),
            new FragmentAddPhrase()
    };


    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return fragments.length;
    }

    @Override
    public Fragment getItem(int position) {
        if(position < fragments.length){
            return fragments[position];
        }
        return null;
    }

}
