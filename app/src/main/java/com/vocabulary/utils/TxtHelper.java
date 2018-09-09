package com.vocabulary.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.concurrent.ExecutionException;

import io.realm.Realm;

import static com.vocabulary.screens.more.ActivityMore.TAG_DATE;
import static com.vocabulary.screens.more.ActivityMore.TAG_LANGUAGE;
import static com.vocabulary.screens.more.ActivityMore.TAG_NUM_OF_PHRASES;
import static com.vocabulary.screens.more.ActivityMore.TAG_TITLE;

public class TxtHelper {

    private static Vocabulary sVocabulary = null;
    private static File sFile = null;

    protected void exportVocabulary(Activity activity, Realm realm, Vocabulary vocabulary) {

        sVocabulary = vocabulary;

        boolean hasPermission = (ContextCompat.checkSelfPermission(activity.getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);
        } else {
            // You are allowed to write external storage:
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/new_folder";
            File storageDir = new File(path);
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                // This should never happen - log handled exception!
            }
        }

        try {
            new ExportPhrases(realm, activity).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    private static class ExportPhrases extends AsyncTask<Void, Void, Void>
    {
        Activity mActivity;
        Realm mRealm;
        ProgressDialog dialog;
        Vocabulary unmanagedVocabulary;

        public ExportPhrases(Realm realm, Activity activity) {
            this.mRealm = realm;
            this.mActivity = activity;
        }

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            unmanagedVocabulary = mRealm.copyFromRealm(sVocabulary);

            dialog = new ProgressDialog(mActivity.getBaseContext());
            dialog.setMessage("Exporting phrases");
            dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(unmanagedVocabulary.getPhrases().size());
            dialog.create();
            dialog.show();
            super.onPreExecute();
        }

        protected Void doInBackground(Void... args) {
            // do background work here

            sFile = null;
            try {
                String location = "/mnt/sdcard/";
                File root = new File(location, "Vocabulary");
                if (!root.exists()) {
                    root.mkdirs();
                }

                String fileName = unmanagedVocabulary.getLanguage() + "_" + unmanagedVocabulary.getTitle() + ".txt";
                sFile = new File(root, fileName);
                //FileWriter writer = add_new FileWriter(gpxfile);
                //TODO encoding?  most utf-8
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sFile)));

                writer.write(TAG_LANGUAGE + unmanagedVocabulary.getLanguage());
                writer.newLine();
                writer.write(TAG_TITLE + unmanagedVocabulary.getTitle());
                writer.newLine();
                writer.write(TAG_DATE + unmanagedVocabulary.getDateAndTime());
                writer.newLine();
                writer.write(TAG_NUM_OF_PHRASES + String.valueOf(unmanagedVocabulary.getPhrases().size()));
                writer.newLine();

                for (Phrase phrase : unmanagedVocabulary.getPhrases())
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
/*
            Snackbar snackbar = Snackbar.make(findViewById(R.id.background), "Exported", Snackbar.LENGTH_SHORT)
                    .setAction("open", add_new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = add_new Intent();
                            Uri uri = FileProvider.getUriForFile(ActivityMore.this, BuildConfig.APPLICATION_ID + ".provider", sFile);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .setData(uri);
                            startActivity(intent);
                        }
                    });
            snackbar.show();*/


            this.cancel(true);
        }

        protected void onCancelled() {
            dialog.dismiss();
            dialog = null;
        }
    }
}
