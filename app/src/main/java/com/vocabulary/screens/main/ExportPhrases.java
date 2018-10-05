package com.vocabulary.screens.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.view.View;

import com.vocabulary.BuildConfig;
import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import io.realm.Realm;

public class ExportPhrases extends AsyncTask<Void, Void, Void> {
    public final static String TAG_LANGUAGE = "language: ";
    public final static String TAG_TITLE = "title: ";
    public final static String TAG_DATE = "crated at: ";
    public final static String TAG_NUM_OF_PHRASES = "number of phrases: ";

    Realm mRealm;
    Context mContext;
    ActivityMain mActivity;
    private Vocabulary mVocabulary;

    File mFile = null;

    ProgressDialog dialog;

    public ExportPhrases(ActivityMain activity) {
        mActivity = activity;
        mContext = activity;
        mRealm = activity.getRealm();
        mVocabulary = activity.getSelectedVocabulary();

        dialog = new ProgressDialog(mContext);
    }

    public File getFile() {
        return mFile;
    }

    @Override
    protected void onPreExecute() {
        //set message of the dialog

        dialog.setMessage("Exporting phrases");
        dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
        dialog.setProgress(0);
        dialog.setMax(mVocabulary.getPhrases().size());
        dialog.create();
        dialog.show();
        super.onPreExecute();
    }

    protected Void doInBackground(Void... args) {
        // do background work here

        mFile = null;
        try {
            String location = "/mnt/sdcard/";
            File root = new File(location, "Vocabulary");
            if (!root.exists()) {
                root.mkdirs();
            }

            String fileName = mVocabulary.getLanguage() + "_" + mVocabulary.getTitle() + ".txt";
            mFile = new File(root, fileName);
            //FileWriter writer = add_new FileWriter(gpxfile);
            //TODO encoding?  most utf-8
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(mFile)));

            writer.write(TAG_LANGUAGE + mVocabulary.getLanguage());
            writer.newLine();
            writer.write(TAG_TITLE + mVocabulary.getTitle());
            writer.newLine();
            writer.write(TAG_DATE + mVocabulary.getDateAndTime());
            writer.newLine();
            writer.write(TAG_NUM_OF_PHRASES + String.valueOf(mVocabulary.getPhrases().size()));
            writer.newLine();

            for (Phrase phrase : mVocabulary.getPhrases())
            {
                writer.write( phrase.getP1().replace("\n", " \\nl ") + " ; ");
                writer.write(phrase.getP2().replace("\n", " \\nl "));
                writer.newLine();
            }

            writer.flush();
            writer.close();

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

        Snackbar snackbar = Snackbar.make(mActivity.findViewById(R.id.coordinator_layout), "Exported", Snackbar.LENGTH_SHORT)
                .setAction("open", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", mFile);
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                .setData(uri);
                        mActivity.startActivity(intent);
                    }
                });
        snackbar.show();


        this.cancel(true);
    }

    protected void onCancelled() {
        dialog.dismiss();
        dialog = null;
    }
}
