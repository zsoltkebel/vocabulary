package com.vocabulary.screens.more;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.BuildConfig;
import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.main.ActivityTabLayout;
import com.vocabulary.screens.merge.ActivityMerge;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmResults;


public class ActivityMore extends AppCompatActivity {

    public final static String TAG_LANGUAGE = "language: ";
    public final static String TAG_TITLE = "title: ";
    public final static String TAG_DATE = "crated at: ";
    public final static String TAG_NUM_OF_PHRASES = "number of phrases: ";

    public final static int SUCCESFUL = 1;
    public final static int CANCELLED = 0;

    @BindView(R.id.tv_language)
            protected TextView languageTextView;
    @BindView(R.id.tv_title)
            protected TextView titleTextView;
    @BindView(R.id.tv_num_of_phrases)
            protected TextView numOfPhrasesTextView;
    @BindView(R.id.tv_date)
            protected TextView dateTextView;
    @BindView(R.id.iv_icon)
            protected ImageView iconImageView;

    Realm realm;
    Vocabulary vocabulary = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        ButterKnife.bind(this);

        realm = Realm.getDefaultInstance();

        String vocabularyId = getIntent().getStringExtra(Vocabulary.ID);
        vocabulary = realm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst();

        iconImageView.setImageDrawable(vocabulary.getIconDrawable(this));
        languageTextView.setText(vocabulary.getLanguageReference(this));
        titleTextView.setText(vocabulary.getTitle());
        titleTextView.setSelected(true); // to scroll text automatically
        numOfPhrasesTextView.setText(String.valueOf(vocabulary.getPhrases().size()));
        try {
            dateTextView.setText(Vocabulary.getDateFormat().format(Vocabulary.getDateAndTimeFormat().parse(vocabulary.getDateAndTime())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    @OnClick(R.id.iv_click_back)
    protected void onBackClicked() {
        finish();
    }

    @OnClick(R.id.lt_click_rename)
    protected void onRenameClicked() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
        final EditText titleEditText = (EditText) dialogView.findViewById(R.id.editText_dic_name);
        titleEditText.setText(vocabulary.getTitle());
        titleEditText.requestFocus();
        titleEditText.selectAll();
        //show keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

        builder.setView(dialogView);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);
            }
        });
        builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (realm.where(Vocabulary.class)
                        .equalTo(Vocabulary.LANGUAGE, vocabulary.getLanguage())
                        .equalTo(Vocabulary.TITLE, titleEditText.getText().toString())
                        .findFirst() == null) {
                    realm.beginTransaction();
                    String title = titleEditText.getText().toString();
                    vocabulary.setTitle(title);
                    realm.commitTransaction();

                    titleTextView.setText(vocabulary.getTitle());
                    //hide keyboard
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);

                    Toast.makeText(ActivityMore.this, R.string.msg_title_change_successful, Toast.LENGTH_SHORT).show();
                } else {

                }
            }
        });
        builder.show();
    }

    @OnClick(R.id.lt_click_delete)
    protected void onDeleteClicked() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.are_you_sure_delete);
        builder.setNegativeButton(R.string.cancel, null);
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String vocabularyId = vocabulary.getId();
                realm.executeTransactionAsync(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        RealmResults<Phrase> phrases = realm.where(Phrase.class).equalTo(Phrase.VOCABULARY_ID, vocabularyId).findAll();
                        phrases.deleteAllFromRealm();
                        Vocabulary vocabulary = realm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst();
                        vocabulary.deleteFromRealm();
                    }
                });
                setResult(ActivityTabLayout.ITEM_DELETED);
                finish();
            }
        });
        builder.show();
    }

    File imported = null;
    @OnClick(R.id.lt_click_export)
    protected void onExportClicked() {
        boolean hasPermission = (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(this,
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
            new ExportPhrases().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.lt_click_share)
    protected void onShareClicked() {
        onExportClicked();
        Uri uri = FileProvider.getUriForFile(ActivityMore.this, BuildConfig.APPLICATION_ID + ".provider", imported);

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/*");

        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));
    }

    private class ExportPhrases extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog = new ProgressDialog(ActivityMore.this);
        Vocabulary unmanagedVocabulary;

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            unmanagedVocabulary = realm.copyFromRealm(vocabulary);

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

            imported = null;
            try {
                String location = "/mnt/sdcard/";
                File root = new File(location, "Vocabulary");
                if (!root.exists()) {
                    root.mkdirs();
                }

                String fileName = unmanagedVocabulary.getLanguage() + "_" + unmanagedVocabulary.getTitle() + ".txt";
                imported = new File(root, fileName);
                //FileWriter writer = new FileWriter(gpxfile);
                //TODO encoding?  most utf-8
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(imported)));

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

            Snackbar snackbar = Snackbar.make(findViewById(R.id.background), "Exported", Snackbar.LENGTH_SHORT)
                    .setAction("open", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            Uri uri = FileProvider.getUriForFile(ActivityMore.this, BuildConfig.APPLICATION_ID + ".provider", imported);
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    .setData(uri);
                            startActivity(intent);
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

    @OnClick(R.id.lt_click_merge)
    protected void onMergeClicked() {
        Intent intent = new Intent(ActivityMore.this, ActivityMerge.class);
        intent.putExtra(Vocabulary.ID, vocabulary.getId());
        startActivityForResult(intent, SUCCESFUL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == requestCode)
            finish();
    }
}
