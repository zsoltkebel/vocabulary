package com.vocabulary.learn;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.Word;

import java.util.ArrayList;

/**
 * Created by Kébel Zsolt on 2018. 03. 24..
 */

public class ListViewAdapter extends BaseAdapter
{
    Context context;
    ArrayList<Word> words;
    boolean phraseAsked;

    public ListViewAdapter(Context context, ArrayList<Word> words, boolean phraseAsked)
    {
        this.context = context;
        this.words = words;
        this.phraseAsked = phraseAsked;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View row;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.test_row, parent, false);

        final WordHolder holder = new WordHolder();
        holder.word = words.get(position);
        holder.tvAsked = (TextView) row.findViewById(R.id.tv_asked_phrase);
        holder.editTextAnswer = (EditText) row.findViewById(R.id.editText_answer);

        holder.tvAsked.setText(phraseAsked ? words.get(position).getWord() : words.get(position).getWord2());

        row.setTag(holder);

        return row;
    }
    private static class WordHolder
    {
        Word word;
        TextView tvAsked;
        EditText editTextAnswer;
    }

    @Override
    public int getCount()
    {
        return words.size();
    }

    @Override
    public Word getItem(int position) {
        return words.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
