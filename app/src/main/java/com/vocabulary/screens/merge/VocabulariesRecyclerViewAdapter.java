package com.vocabulary.screens.merge;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.R;

import io.realm.Realm;
import io.realm.RealmResults;

public class VocabulariesRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private static final int HEADER = 0;
    private static final int VOCABULARY = 1;

    private Realm realm;
    ActivityMerge activity;
    private Context context;
    private RealmResults<Vocabulary> vocabularies;

    VocabulariesRecyclerViewAdapter(ActivityMerge activity, Realm realm) {
        this.activity = activity;
        this.context = activity.getBaseContext();
        this.realm = realm;
        this.vocabularies = activity.getVocabularies();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case HEADER:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_header, parent, false);
                return new ViewHolderHeader(view);
            case VOCABULARY:
                view = LayoutInflater.from(context).inflate(R.layout.list_item_vocabulary, parent,false);
                return new ViewHolderVocabulary(view);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Vocabulary vocabulary = vocabularies.get(position);
        switch (holder.getItemViewType()) {
            case HEADER:
                ViewHolderHeader headerViewHolder = (ViewHolderHeader) holder;

                if (realm.where(Vocabulary.class)
                        .equalTo(Vocabulary.LANGUAGE, vocabulary.getLanguage())
                        .findAll().size() == 1) {
                    RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) headerViewHolder.itemView.getLayoutParams();
                    param.width = 0;
                    param.height = 0;
                    headerViewHolder.itemView.setVisibility(View.GONE);
                    headerViewHolder.itemView.setLayoutParams(param);
                    break;
                } else {
                    RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) headerViewHolder.itemView.getLayoutParams();
                    param.width = LinearLayout.LayoutParams.MATCH_PARENT;
                    param.height = LinearLayout.LayoutParams.WRAP_CONTENT;
                    headerViewHolder.itemView.setVisibility(View.VISIBLE);
                    headerViewHolder.itemView.setLayoutParams(param);
                }

                headerViewHolder.languageTextView.setText(vocabulary.getLanguageReference(context));
                headerViewHolder.iconImageView.setImageDrawable(vocabulary.getIconDrawable(context));
                headerViewHolder.phrasesTextView.setText(
                        String.valueOf(realm.where(Phrase.class)
                                .equalTo(Phrase.LANGUAGE, vocabulary.getLanguage())
                                .findAll().size()));

                headerViewHolder.itemView.setClickable(false);
                break;
            case VOCABULARY:
                final ViewHolderVocabulary listItemViewHolder = (ViewHolderVocabulary) holder;

                listItemViewHolder.titleTextView.setText(vocabulary.getTitle());
                listItemViewHolder.titleTextView.setSelected(true); // to scroll text automatically
                listItemViewHolder.phrasesTextView.setText(
                        String.valueOf(vocabulary.getPhrases().size()));
                listItemViewHolder.clickMark.setVisibility(View.GONE);
                listItemViewHolder.clickMore.setVisibility(View.GONE);

                listItemViewHolder.clickLayoutOpen.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (vocabulary.getId().equals(activity.getVocabularyCurrent().getId()))
                            Toast.makeText(context, R.string.choose_other_vocabulary_msg, Toast.LENGTH_SHORT).show();
                        else
                            activity.setSelectedVocabulary(vocabulary);
                    }
                });

                break;
        }
    }

    @Override
    public int getItemCount() {
        return vocabularies.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (vocabularies.get(position).getId().contains("_"))
            return VOCABULARY;
        else
            return HEADER;
    }

    private int clickCounter = 0;
    private void countClick() {
        if (clickCounter < 3)
            clickCounter++;
        else {
            clickCounter = 0;
            Toast.makeText(context, "Press and hold on a vocabulary to mark it", Toast.LENGTH_LONG).show();
        }
    }

    private void changeMarkState(Vocabulary vocabulary) {
        realm.beginTransaction();
        vocabulary.setMarked(!vocabulary.isMarked());
        realm.commitTransaction();

        if (vocabulary.isMarked())
            Toast.makeText(context, "Marked", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Unmarked", Toast.LENGTH_SHORT).show();
    }
}
