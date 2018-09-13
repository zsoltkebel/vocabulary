package com.vocabulary.screens.learnconfig;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;

import java.util.ArrayList;

public class FragmentConfigChooseCorrect extends LearnConfigFragment {
    private View mRoot;

    @Override
    public void startTest(Context context) {
        Intent intent = new Intent(context, ActivityChooseCorrect.class);
        intent.putExtra(Vocabulary.ID, ((ActivityLearnConfig) getActivity()).getVocabulary().getId());
        intent.putIntegerArrayListExtra(Phrase.STATE, new ArrayList<>(((ActivityLearnConfig) getActivity()).getStateFilters()));
        context.startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = inflater.inflate(R.layout.fragment_flashcasrd_config, container, false);

        return mRoot;
    }

}
