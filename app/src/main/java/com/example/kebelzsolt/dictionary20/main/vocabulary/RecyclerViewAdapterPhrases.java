package com.example.kebelzsolt.dictionary20.main.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.kebelzsolt.dictionary20.ActivityWord;
import com.example.kebelzsolt.dictionary20.R;
import com.example.kebelzsolt.dictionary20.Vocabulary;
import com.example.kebelzsolt.dictionary20.Word;

import java.util.ArrayList;
import java.util.HashSet;

import static com.example.kebelzsolt.dictionary20.Data.mydb;
import static com.example.kebelzsolt.dictionary20.Preferences.getSortType;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;
import static com.example.kebelzsolt.dictionary20.main.vocabulary.ActivityVocabulary.KEY_WORD_ID;
import static com.example.kebelzsolt.dictionary20.main.vocabulary.ActivityVocabulary.vocabularyID;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class RecyclerViewAdapterPhrases extends RecyclerView.Adapter<RecyclerViewAdapterPhrases.ViewHolder>
{
    public final static String VOCABULARY_CODE = "vocabularyCode";

    public static final String PREFS_SORT = "prefsSort";
    public static final String KEY_SORT = "sort";
    public final int SORT_ABC = 1;
    public static final int SORT_DATE = 0;

    private Context context;
    private ActivityVocabulary activity;

    private Vocabulary vocabulary;

    private ArrayList<Word> phrases;
    private HashSet<Integer> selectedIDs;

    private LayoutInflater inflater;
    private FragmentPhraseList fragment;

    private String filter;
    private boolean selectable = false;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        Word word;
        View listItem;
        TextView tvWord1;
        TextView tvWord2;
        TextView tvVocabularyIn;
        CheckBox checkBox;

        public ViewHolder(View listItem)
        {
            super(listItem);
            this.listItem = listItem;
            this.tvWord1 = (TextView) listItem.findViewById(R.id.textView_word1);
            this.tvWord2 = (TextView) listItem.findViewById(R.id.textView_word2);
            this.checkBox = (CheckBox) listItem.findViewById(R.id.checkBox);
        }
    }


    RecyclerViewAdapterPhrases (ActivityVocabulary activity, FragmentPhraseList fragment, Vocabulary vocabulary, int sortType)
    {
        this.context = activity.getBaseContext();
        this.activity = activity;
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
        this.vocabulary = vocabulary;
        this.phrases = getSortedPhrases(sortType);
        this.selectedIDs = new HashSet<>();
        this.filter = "";
    }



    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final ViewHolder holder = new ViewHolder(inflater.inflate(R.layout.row, null));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(KEY_INDEX_OF_VOCABULARY, vocabularyID);
                //TODO real id should be used
                dataBundle.putInt(KEY_WORD_ID, holder.word.getId());

                dataBundle.putString(VOCABULARY_CODE, vocabulary.getCode());

                Intent intent = new Intent(context, ActivityWord.class);
                intent.putExtras(dataBundle);
                context.startActivity(intent);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!selectable)
                {
                    selectedIDs.add(holder.word.getId());
                    setSelectable(true);
                }
                return true;

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewAdapterPhrases.ViewHolder holder, final int position)
    {
        final Word phrase = phrases.get(position);

        holder.word = phrase;
        holder.tvWord1.setText(phrase.getWord());
        holder.tvWord2.setText(phrase.getWord2());

        if (selectable)
        {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(selectedIDs.contains(phrase.getId()));
        }
        else
        {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked())
                    selectedIDs.add(phrase.getId());
                else
                    selectedIDs.remove(phrase.getId());
                //optionsRefresh();
                fragment.updateOptions(selectable, selectedIDs.size());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return phrases.size();
    }

    public void updateList(int sortType)
    {
        phrases.clear();
        phrases = getSortedPhrases(sortType);
        notifyItemRangeChanged(0, getItemCount());
        activity.titleRefresh();
    }

    public void search(final String filter)
    {
        recoverAllRemoved(filter);
        this.filter = filter;

        for (int i = 0; i < phrases.size(); i++)
        {
            if (!phrases.get(i).contains(filter))
            {
                removeItem(i);
                i--;
            }
        }
        //notifyItemRangeChanged(0, vocabulary.getNumOfWords());
    }

    public void deleteItem(final int position)
    {
        mydb.deleteWord(vocabulary.getCode(), vocabulary.getWordList().get(position));
        vocabulary.getWordList().remove(position);
        notifyItemRemoved(position);
    }

    public void removeItem(final int position)
    {
        phrases.remove(position);
        notifyItemRemoved(position);
    }

    public void updateList(Vocabulary vocabulary)
    {
        this.vocabulary = vocabulary;
    }

    public void recoverAllRemoved(final String filter)
    {
        if (filter == null || filter.equals("") || this.filter == null || !filter.contains(this.filter))
        {
            phrases.clear();
            phrases = new ArrayList<>(getSortedPhrases(getSortType(context)));
            notifyItemRangeChanged(0, phrases.size());
        }
    }

    public void setSelectable(boolean selectable)
    {
        this.selectable = selectable;

        if (!this.selectable)
            selectedIDs.clear();

        for (int i = 0; i < getItemCount(); i++)
            notifyItemChanged(i);

        fragment.updateOptions(selectable, selectedIDs.size());
    }

    public boolean getSelectable()
    {
        return selectable;
    }

    public HashSet<Integer> getSelectedIDs()
    {
        return  selectedIDs;
    }

    private ArrayList<Word> getSortedPhrases(int sortType)
    {
        ArrayList<Integer> orderingArray;
        ArrayList<Word> orderedPhrases = new ArrayList<>();
        //TODO egz sort
        switch (sortType)
        {
            case SORT_DATE:
                orderingArray = mydb.getOrderingArrayByDate(vocabulary);
                break;
            case SORT_ABC:
                orderingArray = mydb.getOrderingArrayByABC(vocabulary);
                break;
            default:
                orderingArray = mydb.getOrderingArrayByDate(vocabulary);
        }

        for (int i = 0; i < orderingArray.size(); i++)
            orderedPhrases.add(vocabulary.getWordList().get(orderingArray.get(i)));

        return orderedPhrases;
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return "aha";
    }

}
