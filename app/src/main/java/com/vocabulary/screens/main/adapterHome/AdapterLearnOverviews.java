package com.vocabulary.screens.main.adapterHome;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.vocabulary.R;
import com.vocabulary.realm.LearnOverview;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.main.ActivityMain;
import com.vocabulary.screens.vocabulary.ActivityVocabulary;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class AdapterLearnOverviews extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    ActivityMain activity;

    RealmResults<LearnOverview> mFeedItems;

    Realm mRealm;

    public AdapterLearnOverviews(ActivityMain activity, Realm realm) {
        this.context = activity.getBaseContext();
        this.activity = activity;
        this.mFeedItems = realm.where(LearnOverview.class).findAll().sort(LearnOverview.DATE_OF_LAST_TEST, Sort.DESCENDING);
        this.mRealm = realm;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_learn_overview, parent, false);
        return new ViewHolderLearnOverView(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        LearnOverview learnOverview = mFeedItems.get(position);
        final Vocabulary vocabulary = mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, learnOverview.getVocabularyId()).findFirst();

        ViewHolderLearnOverView holderLearnOverView = (ViewHolderLearnOverView) holder;

        holderLearnOverView.titleTextView.setText(vocabulary.getTitle());
        holderLearnOverView.titleTextView.setSelected(true);
        holderLearnOverView.phrasesTextView.setText(String.valueOf(vocabulary.getPhrases().size()));
        addData(holderLearnOverView.pieChart, vocabulary);
        holderLearnOverView.icon.setBackground(vocabulary.getIconDrawable(context));
        holderLearnOverView.clickMark.setActivated(vocabulary.isMarked());

        holderLearnOverView.clickLayoutOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getFragmentVocabularyList().getProgressDialog().show();

                Intent intent = ActivityVocabulary.createIntent(context, vocabulary.getId());
                activity.startActivityForResult(intent, 0);

                activity.setSelectedVocabulary(vocabulary);
            }
        });

        holderLearnOverView.clickMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.getFragmentVocabularyList().getProgressDialog().show();

                activity.startActivityMore(vocabulary.getId());

                activity.setSelectedVocabulary(vocabulary);

            }
        });

        holderLearnOverView.clickLayoutOpen.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrate();
                changeMarkState(vocabulary);
                return true;
            }
        });
        holderLearnOverView.clickMark.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                vibrate();
                changeMarkState(vocabulary);
                return true;
            }
        });
        holderLearnOverView.clickMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //countClick();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFeedItems.size();
    }

    private void addData(PieChart pieChart, Vocabulary vocabulary) {

        int newP = 0;
        int dontKnowP = 0;
        int kindaP = 0;
        int knowP = 0;

        for (Phrase phrase : vocabulary.getPhrases())
            switch (phrase.calculateState()) {
                case Phrase.NEW:
                    newP++;
                    break;
                case Phrase.DONT_KNOW:
                    dontKnowP++;
                    break;
                case Phrase.KINDA:
                    kindaP++;
                    break;
                case Phrase.KNOW:
                    knowP++;
                    break;
            }

        float yData[] = new float[4];

        yData[0] = (float) newP / (float) (newP + dontKnowP + kindaP + knowP) * (float) 100;
        yData[1] = (float) dontKnowP / (float) (newP + dontKnowP + kindaP + knowP) * (float) 100;
        yData[2] = (float) kindaP / (float) (newP + dontKnowP + kindaP + knowP) * (float) 100;
        yData[3] = (float) knowP / (float) (newP + dontKnowP + kindaP + knowP) * (float) 100;

        ArrayList<PieEntry> yVals1 = new ArrayList<PieEntry>();

        for (int i = 0; i < yData.length; i++)
            yVals1.add(new PieEntry(yData[i]));

        /*
        ArrayList<String> xVals = add_new ArrayList<String>();

        for (int i = 0; i < xData.length; i++)
            xVals.add(xData[i]);
*/
        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, "Market Share");
        dataSet.setSliceSpace(3);
        dataSet.setSelectionShift(5);
        dataSet.setDrawValues(false);

        // add many colors
        ArrayList<Integer> colors = new ArrayList<Integer>();
        colors.add(context.getResources().getColor(R.color.dot_blue));
        colors.add(context.getResources().getColor(R.color.dot_red));
        colors.add(context.getResources().getColor(R.color.dot_yellow));
        colors.add(context.getResources().getColor(R.color.dot_green));
/*
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue()); */
        dataSet.setColors(colors);

        // instantiate pie data object now
        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.GRAY);

        pieChart.setData(data);

        pieChart.setDescription(null);
        pieChart.getLegend().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(false);

        // undo all highlights
        pieChart.highlightValues(null);

        // update pie chart
        pieChart.invalidate();
    }

    private void changeMarkState(Vocabulary vocabulary) {
        mRealm.beginTransaction();
        vocabulary.setMarked(!vocabulary.isMarked());
        mRealm.commitTransaction();

        if (vocabulary.isMarked())
            Toast.makeText(context, "Marked", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Unmarked", Toast.LENGTH_SHORT).show();
    }

    private void vibrate() {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v.hasVibrator())
            v.vibrate(10);
    }

    public RealmResults<LearnOverview> getFeedItems() {
        return mFeedItems;
    }
}
