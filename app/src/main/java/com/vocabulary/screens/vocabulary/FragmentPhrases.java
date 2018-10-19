package com.vocabulary.screens.vocabulary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmFragment;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.edit.EditWord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.PREFS_VOCABULARY;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class FragmentPhrases extends RealmFragment {
    public static final String PREFS_SORTING_FIELD = "sortingField";
    public static final String PREFS_SORTING_ASCENDING = "ascending";

    @BindView(R.id.rv_list) RecyclerView recyclerView;
    @BindView(R.id.tv_numOfSelected) TextView tv_numberOfSelected;
    @BindView(R.id.layout_options) LinearLayout options;
    @BindView(R.id.btn_delSelected) ImageView deleteSelected;
    @BindView(R.id.btn_edit) ImageView editSelected;

    private View mRootView;
    private Vocabulary mVocabulary;

    private AdapterPhrases adapter;

    private ActivityVocabulary mActivityVocabulary;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRootView = inflater.inflate(R.layout.fragment_phrases, container, false);
        ButterKnife.bind(this, mRootView);

        mActivityVocabulary = (ActivityVocabulary) mActivity;

        mContext = getContext();
        mVocabulary = mActivityVocabulary.getVocabulary();

        setAdapter();

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));

        return mRootView;
    }

    public class WrapContentLinearLayoutManager extends LinearLayoutManager {
        public WrapContentLinearLayoutManager(Context context, int recycler, boolean state) {
            super(context, recycler, state);
        }

        //... constructor
        @Override
        public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
            try {
                super.onLayoutChildren(recycler, state);
            } catch (IndexOutOfBoundsException e) {
                Log.e("probe", "meet a IOOBE in RecyclerView");
            }
        }
    }

    final ArrayList<Phrase> trashPhrases = new ArrayList<>();
    final ArrayList<Integer> trashIndexes = new ArrayList<>();

    @OnClick(R.id.btn_delSelected) void onDeleteClicked() {
        trashPhrases.clear();
        trashIndexes.clear();

        //TODO string resource and number of selected items
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setMessage(R.string.are_you_sure_delete);

        dialog.setNegativeButton(R.string.cancel, null);
        dialog.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String vocabularyId = mVocabulary.getId();
                final HashSet<String> idsOfSelected = new HashSet<>(adapter.getIdsOfSelected());
                final List<Phrase> phrases = new ArrayList<>(adapter.getPhrases());

                adapter.setSelectable(false);

                mRealm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(@NonNull Realm realm) {
                        for (String id : idsOfSelected) {
                            Phrase phrase = realm.where(Phrase.class)
                                    .equalTo(Phrase.VOCABULARY_ID, vocabularyId)
                                    .equalTo(Phrase.DATE, id).findFirst();
                            assert phrase != null;
                            trashPhrases.add(realm.copyFromRealm(phrase));
                            trashIndexes.add(phrases.indexOf(phrase));
                            phrase.deleteFromRealm();
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Snackbar snackbar = Snackbar.make(mRootView.findViewById(R.id.background),
                                getResources().getString(R.string.message_deleted), Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        mRealm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(@NonNull Realm realm) {
                                                for (int i =trashPhrases.size() - 1; i >= 0; i--) {
                                                    final Phrase managedPhrase = realm.copyToRealm(trashPhrases.get(i));
                                                    if (realm.where(Vocabulary.class)
                                                            .equalTo(Vocabulary.ID, vocabularyId)
                                                            .findFirst().getPhrases().size() == 0)
                                                        realm.where(Vocabulary.class)
                                                                .equalTo(Vocabulary.ID, vocabularyId)
                                                                .findFirst().getPhrases().add(managedPhrase);
                                                    else
                                                        realm.where(Vocabulary.class)
                                                                .equalTo(Vocabulary.ID, vocabularyId)
                                                                .findFirst().getPhrases().add(trashIndexes.get(i), managedPhrase);
                                                }
                                            }
                                            });
                                        }
                                });
                        snackbar.show();
                    }
                });
            }
        });
        dialog.create().show();
    }

    @OnClick(R.id.btn_edit) void editWord() {
        final HashSet<String> idsOfSelected = adapter.getIdsOfSelected();

        for (String id : idsOfSelected) {
            Phrase phrase = mRealm.where(Phrase.class)
                    .equalTo(Phrase.LANGUAGE, mVocabulary.getLanguage())
                    .equalTo(Phrase.DATE, id).findFirst();

            if (phrase != null) {
                Intent intent = EditWord.createIntent(getContext(), phrase);
                startActivityForResult(intent, 0);
            }
        }
        adapter.setSelectable(false);
    }

    public void setAdapter() {
        if (adapter != null)
            adapter.getPhrases().removeAllChangeListeners();

        adapter = new AdapterPhrases(mActivityVocabulary, getSortingField(), getSorting());
        recyclerView.setAdapter(adapter);

        adapter.getPhrases().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Phrase>>() {
            @Override
            public void onChange(@NonNull RealmResults<Phrase> phrases, @NonNull OrderedCollectionChangeSet changeSet) {

                // For deletions, the adapter has to be notified in reverse order.
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {
                    OrderedCollectionChangeSet.Range range = deletions[i];
                    adapter.notifyItemRangeRemoved(range.startIndex, range.length);
                    mActivityVocabulary.setNumOfPhrasesText();
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    adapter.notifyItemRangeInserted(range.startIndex, range.length);
                    mActivityVocabulary.setNumOfPhrasesText();
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    adapter.notifyItemRangeChanged(range.startIndex, range.length);
                }
            }
        });
    }


    public boolean isSelectable()
    {
        return adapter.isSelectable();
    }

    public void setSelectable(boolean selectable)
    {
        adapter.setSelectable(selectable);
    }

    public void updateOptions(boolean visible, int numberOfSelected)
    {
        if (visible)
            options.setVisibility(View.VISIBLE);
        else
            options.setVisibility(View.GONE);

        tv_numberOfSelected.setText(String.valueOf(numberOfSelected));
    }

    public void setSorting(String sortingField, boolean ascending) {
        SharedPreferences prefs = mActivityVocabulary.getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        prefs.edit().putString(PREFS_SORTING_FIELD, sortingField).apply();
        prefs.edit().putBoolean(PREFS_SORTING_ASCENDING, ascending).apply();
    }

    public String getSortingField() {
        SharedPreferences prefs = mActivityVocabulary.getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getString(PREFS_SORTING_FIELD, Phrase.DATE);
    }

    public boolean getSorting() {
        SharedPreferences prefs = mActivityVocabulary.getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getBoolean(PREFS_SORTING_ASCENDING, false);
    }

    public AdapterPhrases getAdapter() {
        if (adapter == null)
            setAdapter();
        return adapter;
    }

}
