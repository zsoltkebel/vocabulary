package com.vocabulary.screens.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.vocabulary.JSONParser;
import com.vocabulary.R;
import com.vocabulary.Subject;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmFragment;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.main.adapterHome.AdapterLearnOverviews;
import com.vocabulary.screens.more.ActivityMore;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class FragmentHome extends RealmFragment {

    @BindView(R.id.rv_learn_overviews)
    protected RecyclerView mRvLearnOverviews;

    LinearLayout importFromTxt;
    View root;

    AdapterLearnOverviews adapterLearnOverviews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, root);

        adapterLearnOverviews = new AdapterLearnOverviews((ActivityMain) getActivity(), mRealm);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRvLearnOverviews.setLayoutManager(mLayoutManager);
        mRvLearnOverviews.setItemAnimator(new DefaultItemAnimator());
        mRvLearnOverviews.setAdapter(adapterLearnOverviews);

        ((ActivityMain) getActivity()).getVocabularies().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Vocabulary>>() {
            @Override
            public void onChange(RealmResults<Vocabulary> vocabularies, OrderedCollectionChangeSet changeSet) {
                adapterLearnOverviews.notifyDataSetChanged();
            }
        });



        importFromTxt = (LinearLayout) root.findViewById(R.id.importFromTxt);

        importFromTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importVocabulary();
            }
        });

        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                importVocabulary();
            }
        });
        return root;
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
                Spanned infoText = Html.fromHtml(info + " add_new phrase has been mFile");
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

    public View getRoot()
    {
        return root;
    }
}
