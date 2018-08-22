package com.vocabulary.screens.merge;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vocabulary.R;

public class ViewHolderHeader extends RecyclerView.ViewHolder {

    ImageView iconImageView;
    TextView languageTextView;
    TextView phrasesTextView;
    ConstraintLayout clickLayoutOpen;

    public ViewHolderHeader(View itemView) {
        super(itemView);

        iconImageView = (ImageView) itemView.findViewById(R.id.iv_icon);
        languageTextView = (TextView) itemView.findViewById(R.id.tv_language);
        phrasesTextView = (TextView) itemView.findViewById(R.id.tv_num_of_phrases);
        clickLayoutOpen = (ConstraintLayout) itemView.findViewById(R.id.lyt_click_open);
    }
}
