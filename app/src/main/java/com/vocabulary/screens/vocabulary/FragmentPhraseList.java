package com.vocabulary.screens.vocabulary;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vocabulary.DBHelper;
import com.vocabulary.EditWord;
import com.vocabulary.NEW.Vocabulary;
import com.vocabulary.R;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.fragments.BaseFragment;

import static android.content.Context.MODE_PRIVATE;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.KEY_WORD_ID;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.PREFS_INDEX;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.PREFS_VOCABULARY;
import static com.vocabulary.screens.vocabulary.ActivityVocabulary.pbSearch;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class FragmentPhraseList extends BaseFragment
{
    public final static String VOCABULARY_CODE = "vocabularyCode";
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


    int indexOfVocabulary;
    Vocabulary vocabulary;
    public static boolean selectable = false;

    static RecyclerViewAdapterPhrases adapter;

    DBHelper database;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_page_phrases;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View root = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, root);

        context = getContext();
        vocabulary = ((ActivityVocabulary) getActivity()).getVocabulary();

        getPrefs();


        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new RecyclerViewAdapterPhrases((ActivityVocabulary) getActivity(),this, vocabulary);
        recyclerView.setAdapter(adapter);

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
        adapter.search(filter);
        adapter.setSelectable(false);
    }*/

    @OnClick(R.id.btn_delSelected)
    void delete()
    {
        ((ActivityVocabulary) getActivity()).clearFocus();
        ((ActivityVocabulary) getActivity()).clearEditText();
        //adapter.deleteSelectedItems();
        final HashSet<Long> indexesOfSelected = adapter.getIndexesOfSelected();
        //TODO string resource and number of selected items
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setIcon(R.drawable.bin_icon);
        dialog.setTitle(R.string.delete);
        dialog.setMessage(R.string.are_you_sure_delete);

        dialog.setNegativeButton(R.string.cancel, null);
        dialog.setPositiveButton(R.string.btn_delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (Long index : indexesOfSelected)
                    vocabulary.getPhrases().remove(index);

                indexesOfSelected.clear();
                //vocabulary.updatePhraseList(getSortType(context));
                //adapter.updateList(getSortType(context));
                //update();
                adapter.notifyItemRangeChanged(0, vocabulary.getPhrases().size());
            }
        });

        dialog.create().show();
    }

    @OnClick(R.id.btn_edit)
    public void editWord()
    {
        final HashSet<Long> indexesOfSelected = adapter.getIndexesOfSelected();

        for (Long index : indexesOfSelected)
        {
            Intent intent = new Intent(getActivity(), EditWord.class);
            Bundle extras = new Bundle();

            extras.putString(KEY_WORD_ID, vocabulary.getPhraseAt(index.intValue()).getDate());
            extras.putString(VOCABULARY_CODE, vocabulary.getId());

            intent.putExtras(extras);
            //startActivityForResult(intent, 0);
            startActivity(intent);
        }
        //TODO list fresh not working
        //vocabulary.updatePhraseList(getSortType(context));
        //adapter.updateList(getSortType(context));

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
        SharedPreferences prefs = getActivity().getSharedPreferences(PREFS_VOCABULARY, MODE_PRIVATE);

        indexOfVocabulary = prefs.getInt(PREFS_INDEX, 0);//"No name defined" is the default value.
        //vocabulary = database.getVocabulary(indexOfVocabulary);
    }

    public void search(String filter)
    {
        //adapter.search(filter);
        recyclerView.getLayoutManager().scrollToPosition(0);
        pbSearch.setVisibility(View.GONE);
    }


    public RecyclerViewAdapterPhrases getAdapter()
    {
        return adapter;
    }

    public boolean isSelectable()
    {
        return adapter.getSelectable();
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


}
