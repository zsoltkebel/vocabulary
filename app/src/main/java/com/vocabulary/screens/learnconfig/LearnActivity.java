package com.vocabulary.screens.learnconfig;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmActivity;

public abstract class LearnActivity extends RealmActivity {
    public static final String P1_ASKED = "p1Asked";

    protected boolean mP1Asked;

    @Override
    protected Activity getActivity() {
        return this;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mP1Asked = getIntent().getBooleanExtra(P1_ASKED, true);
    }

    protected String getAskedString(Phrase phrase) {
        if (mP1Asked)
            return phrase.getP1();
        else
            return phrase.getP2();
    }

    protected String getAnswerString(Phrase phrase) {
        if (mP1Asked)
            return phrase.getP2();
        else
            return phrase.getP1();
    }
}
