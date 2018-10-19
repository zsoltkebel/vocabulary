package com.vocabulary.screens.phrase;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FragmentPhrase extends Fragment {

    @BindView(R.id.txtvWord) TextView mTvP1;
    @BindView(R.id.txtvMeaning) TextView mTvP2;
    @BindView(R.id.tv_state_text) TextView mTvState;
    @BindView(R.id.v_state_color) View mViewStateColor;

    private Phrase mPhrase;

    public static FragmentPhrase newInstance (String vocabularyId, String phraseId) {
        Bundle args = new Bundle();
        args.putString(Phrase.VOCABULARY_ID, vocabularyId);
        args.putString(Phrase.DATE, phraseId);

        FragmentPhrase fragment = new FragmentPhrase();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View mRoot = inflater.inflate(R.layout.fragment_phrase, container, false);
        ButterKnife.bind(this, mRoot);

        String mVocabularyId = getArguments().getString(Phrase.VOCABULARY_ID);
        String mPhraseId = getArguments().getString(Phrase.DATE);

        mPhrase = ((ActivityPhrase) getActivity()).getRealm().where(Phrase.class)
                .equalTo(Phrase.VOCABULARY_ID, mVocabularyId)
                .equalTo(Phrase.DATE, mPhraseId).findFirst();

        mTvP1.setText(mPhrase.getP1());
        mTvP2.setText(mPhrase.getP2());

        switch (mPhrase.calculateState()) {
            case Phrase.NEW:
                mTvState.setText(R.string.state_new);
                mViewStateColor.setBackground(getResources().getDrawable(R.color.dot_blue));
                break;
            case Phrase.DONT_KNOW:
                mTvState.setText(R.string.state_dont_know);
                mViewStateColor.setBackground(getResources().getDrawable(R.color.dot_red));
                break;
            case Phrase.KINDA:
                mTvState.setText(R.string.state_kinda);
                mViewStateColor.setBackground(getResources().getDrawable(R.color.dot_yellow));
                break;
            case Phrase.KNOW:
                mTvState.setText(R.string.state_know);
                mViewStateColor.setBackground(getResources().getDrawable(R.color.dot_green));
                break;
        }

        return mRoot;
    }

    public Phrase getPhrase() {
        return mPhrase;
    }
}
