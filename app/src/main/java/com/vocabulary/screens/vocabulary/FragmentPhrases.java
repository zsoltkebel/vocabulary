package com.vocabulary.screens.vocabulary;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.edit.EditWord;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.fragments.BaseFragment;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static android.content.Context.MODE_PRIVATE;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.PREFS_VOCABULARY;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class FragmentPhrases extends BaseFragment {
    public static final String PREFS_SORTING_FIELD = "sortingField";
    public static final String PREFS_SORTING_ASCENDING = "ascending";

    Context context;

    @BindView(R.id.rv_list)
            protected RecyclerView recyclerView;
    @BindView(R.id.tv_numOfSelected)
            protected TextView tv_numberOfSelected;
    @BindView(R.id.layout_options)
            protected LinearLayout options;
    @BindView(R.id.btn_delSelected)
            protected ImageView deleteSelected;
    @BindView(R.id.btn_edit)
            protected ImageView editSelected;

    private View root;
    private Realm realm;
    private Vocabulary vocabulary;

    private AdapterPhrases adapter;

    private ActivityVocabulary activity;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_phrases;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Activity)
            this.activity =(ActivityVocabulary) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        realm = activity.getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        root = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, root);

        context = getContext();
        vocabulary = activity.getVocabulary();

        getPrefs();


        //recyclerView.setLayoutManager(new LinearLayoutManager(context));

        setAdapter();

        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));


        return root;
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


    @Override
    public void onResume()
    {
        super.onResume();


    }
/*
    public static void update()
    {
        DBHelper dbHelper = new DBHelper(ActivityVocabulary.context);
        adapter.updateList(dbHelper.getVocabulary(vocabularyID));
        adapter.recoverAllRemoved(null);
        adapter.search(mFilter);
        adapter.setSelectable(false);
    }*/

    final ArrayList<Phrase> trashPhrases = new ArrayList<>();
    final ArrayList<Integer> trashIndexes = new ArrayList<>();

    @OnClick(R.id.btn_delSelected)
    protected void delete() {
        trashPhrases.clear();
        trashIndexes.clear();

        //TODO string resource and number of selected items
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(R.string.are_you_sure_delete);

        dialog.setNegativeButton(R.string.cancel, null);
        dialog.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String vocabularyId = vocabulary.getId();
                final HashSet<String> idsOfSelected = new HashSet<>(adapter.getIdsOfSelected());
                final List<Phrase> phrases = new ArrayList<>(adapter.getPhrases());

                adapter.setSelectable(false);

                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        for (String id : idsOfSelected) {
                            Phrase phrase = realm.where(Phrase.class)
                                    .equalTo(Phrase.VOCABULARY_ID, vocabularyId)
                                    .equalTo(Phrase.DATE, id).findFirst();
                            trashPhrases.add(realm.copyFromRealm(phrase));
                            trashIndexes.add(phrases.indexOf(phrase));
                            phrase.deleteFromRealm();
                        }
                    }
                }, new Realm.Transaction.OnSuccess() {
                    @Override
                    public void onSuccess() {
                        Snackbar snackbar = Snackbar.make(root.findViewById(R.id.background),
                                getResources().getString(R.string.message_deleted), Snackbar.LENGTH_LONG)
                                .setAction(R.string.undo, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        realm.executeTransactionAsync(new Realm.Transaction() {
                                            @Override
                                            public void execute(Realm realm) {
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

    @OnClick(R.id.btn_edit)
    public void editWord() {
        final HashSet<String> idsOfSelected = adapter.getIdsOfSelected();

        for (String id : idsOfSelected) {
            Intent intent = new Intent(activity, EditWord.class);
            intent.putExtra(Vocabulary.ID, vocabulary.getId());
            intent.putExtra(Phrase.DATE, id);

            startActivityForResult(intent, 1);
        }
        adapter.setSelectable(false);
    }

    public void listRefresh(int position)
    {
        //adapter.notifyItemRangeChanged(0, vocabulary.getNumOfWords());
        //adapter.notifyItemChanged(position);
        adapter.setSelectable(false);
    }

    private void getPrefs()
    {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);


        //vocabulary = database.getVocabulary(indexOfVocabulary);
    }

    public void search(String filter)
    {
        //adapter.search(mFilter);
        recyclerView.getLayoutManager().scrollToPosition(0);
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
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        prefs.edit().putString(PREFS_SORTING_FIELD, sortingField).apply();
        prefs.edit().putBoolean(PREFS_SORTING_ASCENDING, ascending).apply();
    }

    public String getSortingField() {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getString(PREFS_SORTING_FIELD, Phrase.DATE);
    }

    public boolean getSortingAscending() {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);
        return prefs.getBoolean(PREFS_SORTING_ASCENDING, false);
    }

    public AdapterPhrases getAdapter() {
        if (adapter == null)
            setAdapter();
        return adapter;
    }

    public void setAdapter() {
        try {
            if (adapter != null)
                adapter.getPhrases().removeAllChangeListeners();

            adapter = new AdapterPhrases(activity, getSortingField(), getSortingAscending());
            recyclerView.setAdapter(adapter);

            adapter.getPhrases().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Phrase>>() {
                @Override
                public void onChange(RealmResults<Phrase> phrases, OrderedCollectionChangeSet changeSet) {
                    // `null`  means the async query returns the first time.
                    if (changeSet == null) {
                        adapter.notifyDataSetChanged();
                        return;
                    }
                    // For deletions, the adapter has to be notified in reverse order.
                    OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                    for (int i = deletions.length - 1; i >= 0; i--) {
                        OrderedCollectionChangeSet.Range range = deletions[i];
                        adapter.notifyItemRangeRemoved(range.startIndex, range.length);
                        try {
                            activity.setNumOfPhrasesText();
                        } catch (Exception e) {
                        }
                    }

                    OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                    for (OrderedCollectionChangeSet.Range range : insertions) {
                        adapter.notifyItemRangeInserted(range.startIndex, range.length);
                        try {
                            activity.setNumOfPhrasesText();
                        } catch (Exception e) {
                        }
                    }

                    OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                    for (OrderedCollectionChangeSet.Range range : modifications) {
                        adapter.notifyItemRangeChanged(range.startIndex, range.length);
                    }
                }
            });
        } catch (Exception e) {}
    }


}
