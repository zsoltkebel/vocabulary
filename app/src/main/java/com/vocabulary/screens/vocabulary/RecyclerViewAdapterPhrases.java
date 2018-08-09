package com.vocabulary.screens.vocabulary;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.vocabulary.ActivityWord;
import com.vocabulary.NEW.Phrase;
import com.vocabulary.NEW.Vocabulary;
import com.vocabulary.R;

import java.util.HashSet;

import static com.vocabulary.screens.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.KEY_WORD_ID;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.vocabularyID;

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

    private HashSet<Long> indexesOfSelected;

    private LayoutInflater inflater;
    private FragmentPhraseList fragment;

    private String filter;
    private boolean selectable = false;

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        // each data item is just a string in this case
        Phrase phrase;
        View listItem;
        TextView tvWord1;
        TextView tvWord2;
        TextView tvVocabularyIn;
        CheckBox checkBox;

        public ViewHolder(View listItem)
        {
            super(listItem);
            this.listItem = listItem;
            this.tvWord1 = (TextView) listItem.findViewById(R.id.tv_p1);
            this.tvWord2 = (TextView) listItem.findViewById(R.id.tv_p2);
            this.checkBox = (CheckBox) listItem.findViewById(R.id.checkBox);
        }
    }


    RecyclerViewAdapterPhrases (ActivityVocabulary activity, FragmentPhraseList fragment, Vocabulary vocabulary)
    {
        this.context = activity.getBaseContext();
        this.activity = activity;
        this.fragment = fragment;
        this.inflater = LayoutInflater.from(context);
        this.vocabulary = vocabulary;
        this.indexesOfSelected = new HashSet<>();
        this.filter = "";
    }



    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final ViewHolder holder = new ViewHolder(inflater.inflate(R.layout.list_item_phrase, null));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle dataBundle = new Bundle();
                dataBundle.putInt(KEY_INDEX_OF_VOCABULARY, vocabularyID);
                //TODO real id should be used
                dataBundle.putString(KEY_WORD_ID, holder.phrase.getDate());

                dataBundle.putString(VOCABULARY_CODE, vocabulary.getId());

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
                    //indexesOfSelected.add(holder.phrase.getId());
                    setSelectable(true);
                }
                return true;

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        holder.phrase = vocabulary.getPhraseAt(position);
        holder.tvWord1.setText(holder.phrase.getP1());
        holder.tvWord2.setText(holder.phrase.getP2());

        if (selectable)
        {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(indexesOfSelected.contains((long)position));
        }
        else
        {
            holder.checkBox.setVisibility(View.GONE);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.checkBox.isChecked())
                    indexesOfSelected.add((long)position);
                else
                    indexesOfSelected.remove((long)position);
                //optionsRefresh();
                fragment.updateOptions(selectable, indexesOfSelected.size());
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return vocabulary.getPhrases().size();
    }

/*
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
*/
    public void setSelectable(boolean selectable)
    {
        this.selectable = selectable;

        if (!this.selectable)
            indexesOfSelected.clear();

        for (int i = 0; i < getItemCount(); i++)
            notifyItemChanged(i);

        fragment.updateOptions(selectable, indexesOfSelected.size());
    }

    public boolean getSelectable()
    {
        return selectable;
    }

    public HashSet<Long> getIndexesOfSelected()
    {
        return indexesOfSelected;
    }


    // convenience method for getting data at click position
    String getItem(int id) {
        return "aha";
    }

}
