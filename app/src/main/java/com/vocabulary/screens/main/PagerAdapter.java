package com.vocabulary.screens.main;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class PagerAdapter extends FragmentPagerAdapter {

    private Fragment[] fragments = new Fragment[]{
            new FragmentImport(),
            new FragmentVocabularyList(),
            new FragmentAddVocabulary()
    };

    private Context context;

    public PagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
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
