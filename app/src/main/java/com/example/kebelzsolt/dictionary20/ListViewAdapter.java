package com.example.kebelzsolt.dictionary20;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.kebelzsolt.dictionary20.main.vocabulary.ActivityVocabulary;

import java.util.ArrayList;

import static com.example.kebelzsolt.dictionary20.ActivityVocabularyList.KEY_CODE_OF_VOCABULARY;
import static com.example.kebelzsolt.dictionary20.Data.vocabularies;
import static com.example.kebelzsolt.dictionary20.Data.numOfSelectedItems;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;

/**
 * Created by Kébel Zsolt on 1/8/2017.
 */

public class ListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Word> wordList;
    boolean selectable;
    int i;

    public ListViewAdapter(Context context, ArrayList<Word> wordList, boolean selectable, int i) {
        this.context = context;
        this.wordList = wordList;
        this.selectable = selectable;
        this.i = i;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.row, parent, false);

        final WordHolder holder = new WordHolder();
        holder.word = wordList.get(position);
        holder.tvWord1 = (TextView) row.findViewById(R.id.textView_word1);
        holder.tvWord2 = (TextView) row.findViewById(R.id.textView_word2);
        holder.tvVocabularyIn = (TextView) row.findViewById(R.id.tv_vocabularyIn);
        holder.checkBox = (CheckBox) row.findViewById(R.id.checkBox);

        if (vocabularies.get(i).isContainer())
        {
            holder.tvVocabularyIn.setVisibility(View.VISIBLE);
            holder.tvVocabularyIn.setText(wordList.get(position).getVocabularyIn().getTitle());
            holder.tvVocabularyIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString(KEY_CODE_OF_VOCABULARY, holder.word.getVocabularyIn().getCode());
                    dataBundle.putInt(KEY_INDEX_OF_VOCABULARY, Data.getIndexOfVocabulary(holder.word.getVocabularyIn().getCode()));
                    Intent intent = new Intent(context, ActivityVocabulary.class);
                    intent.putExtras(dataBundle);
                    context.startActivity(intent);
                }
            });
        }
        else
        {
            holder.tvVocabularyIn.setVisibility(View.GONE);
        }

        if (selectable)
            holder.checkBox.setVisibility(View.VISIBLE);
        else
            holder.checkBox.setVisibility(View.GONE);

        row.setTag(holder);
        holder.tvWord1.setText(holder.word.getWord());
        holder.tvWord2.setText(holder.word.getWord2());


        holder.checkBox.setChecked(wordList.get(position).isSelected());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vocabularies.get(i).getWordList().get(wordList.get(position).getIndexList()).setSelected(isChecked);
                if (isChecked)
                    numOfSelectedItems++;
                else
                    numOfSelectedItems--;
                //optionsRefresh();
            }
        });

        return row;


    }

    public static class WordHolder {
        Word word;
        TextView tvWord1;
        TextView tvWord2;
        TextView tvVocabularyIn;
        CheckBox checkBox;

    }

    @Override
    public int getCount() {
        return wordList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
