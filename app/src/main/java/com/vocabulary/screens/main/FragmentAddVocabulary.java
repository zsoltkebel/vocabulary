package com.vocabulary.screens.main;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.vocabulary.JSONParser;
import com.vocabulary.R;
import com.vocabulary.realm.Vocabulary;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.fragments.BaseFragment;
import io.realm.Realm;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class FragmentAddVocabulary extends BaseFragment
{
    @BindView(R.id.iv_click_back)
    protected ImageView mIvClickBack;

    @BindView(R.id.lv_flags)
    protected ListView mListView;

    private View mRoot;

    private ActivityTabLayout mActivity;

    private Realm mRealm;

    private LanguageMenuListViewAdapter adapter;

    private boolean mDuplicatingAccepted = false;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_add_vocabulary;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mRealm = ((ActivityTabLayout) getActivity()).getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = inflater.inflate(R.layout.fragment_add_vocabulary, container, false);
        mActivity = (ActivityTabLayout) getActivity();
        ButterKnife.bind(this, mRoot);

        adapter = new LanguageMenuListViewAdapter(getContext(), JSONParser.getSubjects(getContext()));
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog dialog;
                final View dialog_layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
                //NEW_-----------------------------------------

                // 1. Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle(R.string.new_vocabulary);
                builder.setIcon(JSONParser.getSubjects(getContext()).get(position).getIconDrawable(getContext()));
                builder.setPositiveButton(R.string.add, null);
                builder.setNegativeButton(R.string.btn_cancel, null);
                //Set the layout
                builder.setView(dialog_layout);

                // Create the AlertDialog
                dialog = builder.create();

                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                dialog.show();

                setDuplicatingAccepted(false);

                final EditText dicName = (EditText) dialog.findViewById(R.id.editText_dic_name);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String name = dicName.getText().toString();
                        final String language = adapter.getItem(position).getSubject();

                        final Vocabulary vocabulary = new Vocabulary();
                        vocabulary.set(language, name);

                        if (getDuplicatingAccepted() || mRealm.where(Vocabulary.class).equalTo(Vocabulary.TITLE, vocabulary.getTitle()).findFirst() == null) {
                            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.copyToRealmOrUpdate(vocabulary);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    //hide keyboard
                                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                    imm.hideSoftInputFromWindow(mIvClickBack.getWindowToken(), 0);

                                    Toast.makeText(getContext(), R.string.added_msg, Toast.LENGTH_SHORT).show();

                                    if (mRealm.where(Vocabulary.class)
                                            .equalTo(Vocabulary.LANGUAGE, vocabulary.getLanguage())
                                            .findAll().size() == 2)
                                        ((ActivityTabLayout) getActivity()).getFragmentVocabularyList().notifyRecyclerViewItemRangeChanged();

                                    dialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(getContext(), R.string.vocabulary_already_existing_msg,
                                    Toast.LENGTH_LONG).show();
                            setDuplicatingAccepted(true);
                        }
                    }
                });
            }
        });

        mIvClickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });

        return mRoot;
    }

    public View getRoot()
    {
        return mRoot;
    }

    public boolean getDuplicatingAccepted() {
        return mDuplicatingAccepted;
    }

    public void setDuplicatingAccepted(boolean duplicatingAccepted) {
        this.mDuplicatingAccepted = duplicatingAccepted;
    }
}
