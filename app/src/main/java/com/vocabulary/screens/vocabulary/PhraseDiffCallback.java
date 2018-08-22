package com.vocabulary.screens.vocabulary;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import com.vocabulary.realm.Phrase;

import java.util.List;

public class PhraseDiffCallback extends DiffUtil.Callback {

    private final List<Phrase> oldphrases;
    private final List<Phrase> newPhrases;

    public PhraseDiffCallback(List<Phrase> oldphrases, List<Phrase> newPhrases) {
        this.oldphrases = oldphrases;
        this.newPhrases = newPhrases;
    }

    @Override
    public int getOldListSize() {
        return oldphrases.size();
    }

    @Override
    public int getNewListSize() {
        return newPhrases.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldphrases.get(oldItemPosition).getDate().equals(newPhrases.get(newItemPosition).getDate());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Phrase oldPhrase = oldphrases.get(oldItemPosition);
        final Phrase newPhrase = newPhrases.get(newItemPosition);

        if (!oldPhrase.getP1().equals(newPhrase.getP1()))
            return false;
        else if (!oldPhrase.getP2().equals(newPhrase.getP2()))
            return false;
        else
            return true;
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
