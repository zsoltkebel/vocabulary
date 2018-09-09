package com.vocabulary.screens;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vocabulary.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentVocabularyInfo extends Fragment {
    final static private String ICON_RESOURCE = "iconResource";
    final static private String TITLE = "title";
    final static private String NUM_OF_PHRASES = "numOfPhrases";

    @BindView(R.id.imageView_wordList_flag)
    protected ImageView mIvIcon;

    @BindView(R.id.txt_language)
    protected TextView mTvTitle;
    @BindView(R.id.txt_num_of_words)
    protected TextView mTvNumOfPhrases;

    private View mRoot;

    public static FragmentVocabularyInfo newInstance(int iconInt, String title, int numOfPhrases) {
        Bundle args = new Bundle();
        args.putInt(ICON_RESOURCE, iconInt);
        args.putString(TITLE, title);
        args.putInt(NUM_OF_PHRASES, numOfPhrases);

        FragmentVocabularyInfo fragment = new FragmentVocabularyInfo();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = inflater.inflate(R.layout.fragment_vocabulary_info, container, false);
        ButterKnife.bind(this, mRoot);

        mIvIcon.setImageDrawable(getContext().getDrawable(getArguments().getInt(ICON_RESOURCE)));
        mTvTitle.setText(getArguments().getString(TITLE));
        // to scroll automatically if the text is too long
        mTvTitle.setSelected(true);
        mTvNumOfPhrases.setText(String.valueOf(getArguments().getInt(NUM_OF_PHRASES)));

        return mRoot;
    }

    public void displayFragment(FragmentActivity activity, View frameLayout) {
        FragmentTransaction t = activity.getSupportFragmentManager().beginTransaction();
        t.replace(frameLayout.getId(), this);
        t.commit();
    }

    public void updateInfo(String title, int numOfPhrases) {
        mTvTitle.setText(title);
        // to scroll automatically if the text is too long
        mTvTitle.setSelected(true);
        mTvNumOfPhrases.setText(String.valueOf(numOfPhrases));
    }
}
