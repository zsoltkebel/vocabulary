package com.vocabulary.screens.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
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
import com.vocabulary.Subject;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.more.ActivityMore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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

    private ActivityMain mActivity;

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

        mRealm = ((ActivityMain) getActivity()).getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mRoot = inflater.inflate(R.layout.fragment_add_vocabulary, container, false);
        mActivity = (ActivityMain) getActivity();
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
                                        ((ActivityMain) getActivity()).getFragmentVocabularyList().notifyRecyclerViewItemRangeChanged();

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



    @OnClick(R.id.lt_click_import)
    protected void onImportClicked() {
        importVocabulary();
    }

    public void importVocabulary()
    {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/plain");
        startActivityForResult(intent, 0);
    }

    //TODO
    int numOfPhrases = 0;
    Uri uri = null;
    InputStream inputStream = null;
    BufferedReader reader = null;
    Vocabulary importedVocabulary = null;

    boolean old = false;
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;
        try {
            uri = data.getData();

            //encode anscii to utf-8

            inputStream = getActivity().getContentResolver().openInputStream(uri);
            //TODO encoding? most utf-8
            reader = new BufferedReader(new InputStreamReader(inputStream));

            if (false)
            {
                Toast.makeText(getContext(), "Wrong format in txt", Toast.LENGTH_SHORT).show();
                return;
            }

            String language;
            String name;
            String firstLine = reader.readLine();
            if (firstLine.contains("_")) {
                old = true;
                language = firstLine.substring(0, firstLine.indexOf("_"));
                name = firstLine.substring(firstLine.indexOf("_") + 1);
            } else {
                old = false;
                language = firstLine.replaceAll(ActivityMore.TAG_LANGUAGE, "");
                name = reader.readLine().replace(ActivityMore.TAG_TITLE, "");
                reader.readLine();
            }

            ArrayList<Subject> subjects = JSONParser.getSubjects(getContext());
            for (int i = 0; i < subjects.size(); i++) {
                if (subjects.get(i).getSubject().contains(language))
                    language = subjects.get(i).getSubject();
            }

            importedVocabulary = new Vocabulary();
            importedVocabulary.set(language, name);

            mRealm.executeTransactionAsync(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(importedVocabulary);
                }
            }, new Realm.Transaction.OnSuccess() {
                @Override
                public void onSuccess() {
                    new importPhrases().execute();
                }
            });

            try {
                numOfPhrases = Integer.valueOf(reader.readLine().replace(ActivityMore.TAG_NUM_OF_PHRASES, ""));
            } catch (Exception exception) {
                Toast.makeText(getContext(), "Wrong format in txt", Toast.LENGTH_SHORT).show();
                return;
            }




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private class importPhrases extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog = new ProgressDialog(getContext());
        private int numOfNewPhrases = 0;

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            //TODO make it use string resource
            dialog.setMessage("Importing phrases");
            dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
            dialog.setCancelable(false);
            dialog.setProgress(0);
            dialog.setMax(numOfPhrases);
            dialog.create();
            dialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            // do background work here
            try
            {
                Realm realm = Realm.getDefaultInstance();
                Vocabulary vocabulary = realm.where(Vocabulary.class).equalTo(Vocabulary.ID, importedVocabulary.getId()).findFirst();

                String line = reader.readLine();
                while (line != null)
                {
                    String[] arr = line.split(";");
                    if (arr.length == 3) {
                        final Phrase phrase = new Phrase();
                        phrase.set(arr[1].replace(" \\nl ", "\n"),
                                arr[2].replace(" \\nl ", "\n"), vocabulary);

                        realm.beginTransaction();
                        final Phrase managedPhrase = realm.copyToRealm(phrase);
                        vocabulary.getPhrases().add(managedPhrase);
                        realm.commitTransaction();

                        numOfNewPhrases++;
                    } else if (arr.length != 2) {
                        continue;
                    }
                    else {
                        final Phrase phrase = new Phrase();
                        phrase.set(arr[0].replace(" \\nl ", "\n"),
                                arr[1].replace(" \\nl ", "\n"), vocabulary);

                        realm.beginTransaction();
                        final Phrase managedPhrase = realm.copyToRealm(phrase);
                        vocabulary.getPhrases().add(managedPhrase);
                        realm.commitTransaction();

                        numOfNewPhrases++;
                    }
                    dialog.incrementProgressBy(1);
                    line = reader.readLine();
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            // do UI work here
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
            }
            dialog.dismiss();
            dialog = null;
            if (numOfNewPhrases > 0)
            {
                String info = "<font color='#e62e00'><b>" + numOfNewPhrases + "</b></font>";
                Spanned infoText = Html.fromHtml(info + " add_new phrase has been imported");
                Toast.makeText(getContext(), infoText, Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getContext(), "There's no add_new phrase", Toast.LENGTH_SHORT).show();
            }

            this.cancel(true);
        }

        protected void onCancelled() {
            dialog.dismiss();
            dialog = null;
        }
    }
}
