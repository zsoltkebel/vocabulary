package com.vocabulary.screens.learnconfig;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vocabulary.R;
import com.vocabulary.learn.LearnActivity;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;

import java.util.ArrayList;
import java.util.HashSet;

public class FragmentFlashCardConfig extends LearnConfigFragment {

    private View mRoot;

    private HashSet<Integer> mStateFilters = new HashSet<>();

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = inflater.inflate(R.layout.fragment_flashcasrd_config, container, false);

        return mRoot;
    }

    @Override
    public void startTest(Context context) {
        Intent intent = new Intent(context, LearnActivity.class);
        intent.putExtra(Vocabulary.ID, ((ActivityLearnConfig) getActivity()).getIndexOfVocabulary());
        intent.putIntegerArrayListExtra(Phrase.STATE, new ArrayList<>(((ActivityLearnConfig) getActivity()).getStateFilters()));
        context.startActivity(intent);
    }






}
