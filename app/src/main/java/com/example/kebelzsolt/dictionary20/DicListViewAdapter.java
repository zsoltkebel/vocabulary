package com.example.kebelzsolt.dictionary20;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static com.example.kebelzsolt.dictionary20.Data.getIndexOfSubject;
import static com.example.kebelzsolt.dictionary20.Data.languageTillIndex;

/**
 * Created by Kébel Zsolt on 1/18/2017.
 */

public class DicListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Vocabulary> dicList;

    public DicListViewAdapter(Context context, ArrayList<Vocabulary> dicList) {
        this.context = context;
        Data.orderDicList();
        this.dicList = dicList;
    }

    boolean newLang = true;
    String lang = "";

    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        Holder holder = null;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();



        if (!dicList.get(position).isContainer()) {
            row = inflater.inflate(R.layout.dic_row_simple, parent, false);

            holder = new Holder();
            holder.dic = dicList.get(position);
            holder.tvLanguage = (TextView) row.findViewById(R.id.tv_language);
            holder.tvNumberOfWords = (TextView) row.findViewById(R.id.tv_numberOfWords);

            // to scroll automatically if the text is too long
            holder.tvLanguage.setSelected(true);
            //TODO NEW
            holder.back = (LinearLayout) row.findViewById(R.id.back);
            if (position < dicList.size()-1 && !dicList.get(position).getLanguage().equals(dicList.get(position + 1).getLanguage()))
            {
                if (holder.dic.getType() == Subject.LANGUAGE)
                    holder.back.setBackgroundResource(R.drawable.container_bottom_light);
                else
                    holder.back.setBackgroundResource(R.drawable.container_bottom_dark);

            }else if (position == dicList.size()-1){
                if (holder.dic.getType() == Subject.LANGUAGE)
                    holder.back.setBackgroundResource(R.drawable.container_bottom_light);
                else
                    holder.back.setBackgroundResource(R.drawable.container_bottom_dark);
            } else {
                if (holder.dic.getType() == Subject.LANGUAGE)
                    holder.back.setBackgroundResource(R.drawable.container_middle_light);
                else
                    holder.back.setBackgroundResource(R.drawable.container_middle_dark);
            }



            row.setTag(holder);
            int i;
            for (i = 0; i < holder.dic.getCode().length() && holder.dic.getCode().charAt(i) != '_'; i++) {}
            holder.tvLanguage.setText(holder.dic.getTitle());
            holder.tvNumberOfWords.setText(String.valueOf(holder.dic.getNumOfWords()));
            //int vocabularyID;
            for (i = 0; i < holder.dic.getCode().length() && holder.dic.getCode().charAt(i) != '_'; i++) {

            }
            if (!lang.equals(holder.dic.getCode().substring(0, i))) {
                newLang = true;

            }
            row.setClickable(false);
        } else {

            row = inflater.inflate(R.layout.dic_row, parent, false);

            holder = new Holder();
            holder.dic = dicList.get(position);
            holder.tvLanguage = (TextView) row.findViewById(R.id.tv_language);
            holder.tvNumberOfWords = (TextView) row.findViewById(R.id.tv_numberOfWords);
            holder.imFlag = (ImageView) row.findViewById(R.id.im_flag);

            /////NEWW
            holder.back = (LinearLayout) row.findViewById(R.id.back);
            if (getIndexOfSubject(holder.dic.getSubject()) < languageTillIndex)
                holder.back.setBackgroundResource(R.drawable.container_top_liht);
            else
                holder.back.setBackgroundResource(R.drawable.container_top_dark);



            row.setTag(holder);
            holder.tvLanguage.setText(holder.dic.getSubjectResourceString(context));
            holder.imFlag.setImageDrawable(holder.dic.getIconDrawable(context));
            holder.tvNumberOfWords.setText(String.valueOf(holder.dic.getNumOfWords()));

            row.setClickable(false);
        }
        return row;
    }

    public static class Holder {
        Vocabulary dic;
        TextView tvLanguage;
        TextView tvNumberOfWords;
        ImageView imFlag;

        LinearLayout back;
    }

    @Override
    public int getCount() {
        return dicList.size();
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

