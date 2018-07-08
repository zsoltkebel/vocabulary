package com.example.kebelzsolt.dictionary20.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.kebelzsolt.dictionary20.Data;
import com.example.kebelzsolt.dictionary20.R;
import com.example.kebelzsolt.dictionary20.Vocabulary;
import com.example.kebelzsolt.dictionary20.Word;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import droidninja.filepicker.fragments.BaseFragment;

import static com.example.kebelzsolt.dictionary20.Data.getIndexOfVocabulary;
import static com.example.kebelzsolt.dictionary20.Data.vocabularies;
import static com.example.kebelzsolt.dictionary20.main.ActivityTabLayout.fresh;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.listRefresh;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class FragmentImport extends BaseFragment
{
    LinearLayout importFromTxt;
    View root;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_page_import, container, false);

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

    @Override
    protected int getFragmentLayout() {
        return 0;
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
            String line = reader.readLine();

            if (!Data.isSubject(line))
            {
                Toast.makeText(getContext(), "Wrong format in txt", Toast.LENGTH_SHORT).show();
                return;
            }
            importedVocabulary = new Vocabulary(line);

            if (Data.addVocabuary(line))
                Data.updateVocabularies();

            try {
                numOfPhrases = Integer.valueOf(reader.readLine());
            } catch (Exception exception) {
                Toast.makeText(getContext(), "Wrong format in txt", Toast.LENGTH_SHORT).show();
                return;
            }

            new importPhrases().execute();


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

                String line = reader.readLine();
                while (line != null)
                {
                    String[] arr = line.split(";");
                    if (arr.length != 3)
                    {
                        return null;
                    }
                    if (vocabularies.get(getIndexOfVocabulary(importedVocabulary.getCode())).addPhrase(new Word(arr[1].replace(" \\nl ", "\n"),
                            arr[2].replace(" \\nl ", "\n"))) == Vocabulary.NEW)
                    {
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
                Spanned infoText = Html.fromHtml(info + " new phrase has been imported");
                Toast.makeText(getContext(), infoText, Toast.LENGTH_SHORT).show();
                listRefresh();
                fresh();
            }
            else
            {
                Toast.makeText(getContext(), "There's no new phrase", Toast.LENGTH_SHORT).show();
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
