package com.vocabulary.screens.main;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.Subject;

import java.util.ArrayList;

/**
 * Created by Kébel Zsolt on 1/18/2017.
 */

public class LanguageMenuListViewAdapter extends BaseAdapter {

    ArrayList<Subject> subjects;
    Context context;

    public LanguageMenuListViewAdapter(Context context, ArrayList<Subject> subjects) {
        this.subjects = subjects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return subjects.size();
    }

    @Override
    public Subject getItem(int position) {
        return subjects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        Holder holder;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(R.layout.languages_row, parent, false);

        holder = new Holder();
        holder.imFlag = (ImageView) row.findViewById(R.id.iv_icon);
        holder.tvLanguage = (TextView) row.findViewById(R.id.tv_language);

        row.setTag(holder);
        holder.imFlag.setImageDrawable(subjects.get(position).getIconDrawable(context));
        holder.tvLanguage.setText(subjects.get(position).getSubjectResourceString(context));
        return row;
    }

    public static class Holder {
        ImageView imFlag;
        TextView tvLanguage;
    }
}
