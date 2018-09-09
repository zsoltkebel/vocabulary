package com.vocabulary.screens.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.phrase.ActivityPhrase;

import java.util.HashSet;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class AdapterPhrases extends RecyclerView.Adapter<AdapterPhrases.ViewHolder> {

    private Context mContext;

    private RealmResults<Phrase> mPhrases;

    private HashSet<String> mIdsOfSelected = new HashSet<>();

    private FragmentPhrases mFragment;

    private boolean mSelectable = false;

    private String mVocabularyId;
    private String mFilter;


    protected static class ViewHolder extends RecyclerView.ViewHolder {
        Phrase phrase;
        TextView tvWord1;
        TextView tvWord2;
        View vState;
        CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            this.tvWord1 = (TextView) itemView.findViewById(R.id.tv_p1);
            this.tvWord2 = (TextView) itemView.findViewById(R.id.tv_p2);
            this.vState = (View) itemView.findViewById(R.id.v_state);
            this.checkBox = (CheckBox) itemView.findViewById(R.id.checkBox);
        }
    }

    AdapterPhrases(ActivityVocabulary activity, String sortingField, boolean ascending) {
        this.mContext = activity.getBaseContext();
        this.mFragment = activity.getFragmentPhrases();
        this.mVocabularyId = activity.getVocabulary().getId();

        Realm realm = Realm.getDefaultInstance();
        if (ascending)
            this.mPhrases = realm.where(Phrase.class)
                    .equalTo(Phrase.VOCABULARY_ID, mVocabularyId)
                    .findAll()
                    .sort(sortingField, Sort.ASCENDING);
        else
            this.mPhrases = realm.where(Phrase.class)
                    .equalTo(Phrase.VOCABULARY_ID, mVocabularyId)
                    .findAll()
                    .sort(sortingField, Sort.DESCENDING);
        realm.close();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_phrase, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectable) {
                    if (holder.checkBox.isChecked()) {
                        mIdsOfSelected.remove(holder.phrase.getDate());
                        holder.checkBox.setChecked(false);
                    } else {
                        mIdsOfSelected.add(holder.phrase.getDate());
                        holder.checkBox.setChecked(true);
                    }
                } else {
                    Intent intent = new Intent(mContext, ActivityPhrase.class);
                    intent.putExtra(Vocabulary.ID, holder.phrase.getVocabularyId());
                    intent.putExtra(Phrase.DATE, holder.phrase.getDate());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(intent);
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!mSelectable) {
                    mIdsOfSelected.add(holder.phrase.getDate());
                    setSelectable(true);
                }
                return true;
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.phrase = mPhrases.get(position);

        if (mFilter != null && !mFilter.equals("")) {
            Spannable coloredP1 = new SpannableString(holder.phrase.getP1());
            Spannable coloredP2 = new SpannableString(holder.phrase.getP2());

            int startIndex;
            int endIndex;
            int diff;
            int highlightColor = mContext.getResources().getColor(R.color.transparent_black);

            if (holder.phrase.getP1().toLowerCase().contains(mFilter)) {
                diff = 0;
                String p1 = coloredP1.toString().toLowerCase();

                while (p1.contains(mFilter)) {
                    startIndex = p1.indexOf(mFilter) + diff;
                    endIndex = startIndex + mFilter.length();

                    coloredP1.setSpan(new BackgroundColorSpan(highlightColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    diff += mFilter.length();
                    p1 = p1.replaceFirst(mFilter, "");
                }

                holder.tvWord1.setText(coloredP1);
            } else
                holder.tvWord1.setText(holder.phrase.getP1());

            if (holder.phrase.getP2().toLowerCase().contains(mFilter)) {
                diff = 0;
                String p2 = coloredP2.toString().toLowerCase();

                while (p2.contains(mFilter)) {
                    startIndex = p2.indexOf(mFilter) + diff;
                    endIndex = startIndex + mFilter.length();

                    coloredP2.setSpan(new BackgroundColorSpan(highlightColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    diff += mFilter.length();
                    p2 = p2.replaceFirst(mFilter, "");
                }
                holder.tvWord2.setText(coloredP2);
            } else
                holder.tvWord2.setText(holder.phrase.getP2());

        } else {
            holder.tvWord1.setText(holder.phrase.getP1());
            holder.tvWord2.setText(holder.phrase.getP2());
        }

        if (mSelectable) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(mIdsOfSelected.contains(holder.phrase.getDate()));
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked())
                    mIdsOfSelected.add(holder.phrase.getDate());
                else
                    mIdsOfSelected.remove(holder.phrase.getDate());
            }
        });
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mFragment.updateOptions(mSelectable, mIdsOfSelected.size());
            }
        });

        switch (holder.phrase.calculateState()) {
            case Phrase.NEW:
                holder.vState.setBackground(mContext.getResources().getDrawable(R.drawable.new_dot_blue));
                break;
            case Phrase.DONT_KNOW:
                holder.vState.setBackground(mContext.getResources().getDrawable(R.drawable.new_dot_red));
                break;
            case Phrase.KINDA:
                holder.vState.setBackground(mContext.getResources().getDrawable(R.drawable.new_dot_yellow));
                break;
            case Phrase.KNOW:
                holder.vState.setBackground(mContext.getResources().getDrawable(R.drawable.new_dot_green));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mPhrases.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setSelectable(boolean selectable) {
        this.mSelectable = selectable;

        if (!this.mSelectable)
            mIdsOfSelected.clear();

        for (int i = 0; i < getItemCount(); i++)
            notifyItemChanged(i);

        mFragment.updateOptions(selectable, mIdsOfSelected.size());
    }

    public void setFilter(Realm realm, String filter, String sortingField, boolean ascending) {
        if (filter == null)
            this.mFilter = "";
        else
            this.mFilter = filter.toLowerCase();

        if (ascending)
            this.mPhrases = realm.where(Phrase.class)
                    .equalTo(Phrase.VOCABULARY_ID, mVocabularyId)
                    .beginGroup()
                        .contains(Phrase.P1, mFilter, Case.INSENSITIVE)
                        .or()
                        .contains(Phrase.P2, mFilter, Case.INSENSITIVE)
                    .endGroup()
                    .findAll()
                    .sort(sortingField, Sort.ASCENDING);
        else
            this.mPhrases = realm.where(Phrase.class)
                    .equalTo(Phrase.VOCABULARY_ID, mVocabularyId)
                    .beginGroup()
                    .contains(Phrase.P1, mFilter, Case.INSENSITIVE)
                    .or()
                    .contains(Phrase.P2, mFilter, Case.INSENSITIVE)
                    .endGroup()
                    .findAll()
                    .sort(sortingField, Sort.DESCENDING);
        notifyDataSetChanged();
    }

    public boolean isSelectable() {
        return mSelectable;
    }

    public HashSet<String> getIdsOfSelected() {
        return mIdsOfSelected;
    }

    public RealmResults<Phrase> getPhrases() {
        return mPhrases;
    }


}
