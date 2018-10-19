package com.vocabulary.screens.learnconfig.chooseCorrect;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vocabulary.R;
import com.vocabulary.screens.learnconfig.ActivityLearnConfig;
import com.vocabulary.screens.learnconfig.LearnConfigFragment;

public class FragmentConfigChooseCorrect extends LearnConfigFragment {
    private View mRoot;

    @Override
    public void startTest(Context context) {
        Intent intent = ActivityChooseCorrect.createIntent(
                context,
                ((ActivityLearnConfig) getActivity()).getVocabulary(),
                ((ActivityLearnConfig) getActivity()).getIsP1Asked(),
                ((ActivityLearnConfig) getActivity()).getStates());

        context.startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = inflater.inflate(R.layout.fragment_flashcasrd_config, container, false);

        return mRoot;
    }

}
