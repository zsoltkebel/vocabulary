package com.vocabulary.screens.merge;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vocabulary.R;

public class ViewHolderVocabulary extends RecyclerView.ViewHolder {

    TextView titleTextView;
    TextView phrasesTextView;
    ConstraintLayout clickLayoutOpen;
    ImageView clickMore;
    ImageView clickMark;

    public ViewHolderVocabulary(View itemView) {
        super(itemView);

        titleTextView = (TextView) itemView.findViewById(R.id.txt_language);
        phrasesTextView = (TextView) itemView.findViewById(R.id.txt_num_of_words);
        clickLayoutOpen = (ConstraintLayout) itemView.findViewById(R.id.lyt_click_surface);
        clickMore = (ImageView) itemView.findViewById(R.id.imgv_click_more);
        clickMark = (ImageView) itemView.findViewById(R.id.imgv_click_mark);
    }
}
