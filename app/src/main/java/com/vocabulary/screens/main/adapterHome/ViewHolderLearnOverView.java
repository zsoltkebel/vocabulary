package com.vocabulary.screens.main.adapterHome;

import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.vocabulary.R;

public class ViewHolderLearnOverView extends RecyclerView.ViewHolder {
    TextView titleTextView;
    TextView phrasesTextView;
    ImageView icon;
    PieChart pieChart;
    ConstraintLayout clickLayoutOpen;
    ImageView clickMore;
    ImageView clickMark;

    public ViewHolderLearnOverView(View itemView) {
        super(itemView);

        icon = (ImageView) itemView.findViewById(R.id.iv_icon);
        titleTextView = (TextView) itemView.findViewById(R.id.txt_language);
        phrasesTextView = (TextView) itemView.findViewById(R.id.txt_num_of_words);
        pieChart = (PieChart) itemView.findViewById(R.id.pieChart);
        clickLayoutOpen = (ConstraintLayout) itemView.findViewById(R.id.lyt_click_surface);
        clickMore = (ImageView) itemView.findViewById(R.id.imgv_click_more);
        clickMark = (ImageView) itemView.findViewById(R.id.imgv_click_mark);
    }
}
