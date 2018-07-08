package com.example.kebelzsolt.dictionary20;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static android.content.Context.MODE_PRIVATE;
import static com.example.kebelzsolt.dictionary20.DBHelper.COLUMN_ID;
import static com.example.kebelzsolt.dictionary20.DBHelper.LANGUAGES_COLUMN_LANGUAGE;
import static com.example.kebelzsolt.dictionary20.main.ActivityTabLayout.fresh;

/**
 * Created by Kébel Zsolt on 1/18/2017.
 */

public class Vocabulary extends Subject
{
    public static final String PREFS_SORT = "prefsSort";
    public static final String KEY_SORT = "sort";
    public static final int SORT_ABC = 1;
    public static final int SORT_DATE = 0;

    final public static int EMPTY = -1;
    final public static int EXISTING = 0;
    final public static int NEW = 1;

    private Context context;
    private String code;
    private ArrayList<Word> wordList = new ArrayList<>();
    private int ID;
    DBHelper db;


    public Vocabulary() {
        this.wordList = new ArrayList<>();

    }

    public Vocabulary(Subject subject)
    {
        super(subject.getSubject(), subject.getIconInt(), subject.getType());
        this.wordList = new ArrayList<>();
    }

    public Vocabulary(DBHelper db, Cursor res)
    {
        this.code = res.getString(res.getColumnIndex(LANGUAGES_COLUMN_LANGUAGE));
        this.ID = res.getInt(res.getColumnIndex(COLUMN_ID));
        //this.db = db;
        this.wordList = db.getAllRecords(this);
        setSubject(getLanguage());

    }

    public Vocabulary(String nameOfSubject, String nameOfVocabulary)
    {
        //super(nameOfSubject, getIconInt(nameOfSubject), Data.getTypeOfSubject(nameOfSubject));
        code = nameOfSubject + "_" + nameOfVocabulary.replace(" ", "_").replace(".", "__");
        //TODO
        Data.addVocabuary(code);
    }

    public Vocabulary(String code) {
        this.code = code;
        this.wordList = new ArrayList<>();
    }

    public int getIconInt(Context context, String nameOfSubject)
    {
        ArrayList<Subject> subjects = JSONParser.getSubjects(context);
        for (Subject subject : subjects)
            if (subject.getSubject().equals(nameOfSubject))
                return subject.getIconInt();
        return 0;
    }

    public Drawable getIconDrawable(Context context)
    {
        return context.getDrawable(getIconInt(context, getLanguage()));
    }


    public void setWordList(ArrayList<Word> wordList)
    {
        this.wordList = wordList;
    }

    public int getID()
    {
        return ID;
    }

    // Only works with not ordered list
    public void updatePhraseList(int sortType)
    {
        this.wordList = db.getAllRecords(this);
        /*return;
        ArrayList<Word> newList = mydb.getAllRecords(this);


        ArrayList<Integer> orderingArray = getOrderingArray(sortType);

        ArrayList<Word> orderedPhrases = new ArrayList<>();
        for(int i = 0; i < orderingArray.size(); i++)
        {
            orderedPhrases.add(newList.get(orderingArray.get(i)));
            orderedPhrases.get(i).setIndexList(i);
        }
        newList = orderedPhrases;



        // if items have been deleted
        if (wordList.size() > newList.size())
        {
            // update the phrases
            for (int i = 0; i < newList.size(); i++)
            {
                Word oldPhrase = wordList.get(i);
                Word newPhrase = newList.get(i);
                // if it's the same phrase
                if (oldPhrase.getId() == newPhrase.getId())
                    oldPhrase.update(newPhrase);
                // else it's no longer exists
                else
                    wordList.remove(i);
            }
            // remove any other phrase that no longer exists
            for (int i = newList.size(); i < wordList.size(); i++)
                wordList.remove(i);
        }
        else if (wordList.size() < newList.size())
        {
            // update the phrases
            for (int i = 0; i < wordList.size(); i++)
            {
                Word oldPhrase = wordList.get(i);
                Word newPhrase = newList.get(i);
                // if it's the same phrase
                if (oldPhrase.getId() == newPhrase.getId())
                    oldPhrase.update(newPhrase);
                // else it's no longer exists, and there's a new
                else
                {
                    wordList.remove(i);
                    wordList.add(i, newPhrase);
                }
            }
            // add other new phrases
            for (int i = wordList.size(); i < newList.size(); i++)
                wordList.add(newList.get(i));
        }
        else {
            for (int i = 0; i < wordList.size(); i++) {
                Word oldPhrase = wordList.get(i);
                Word newPhrase = newList.get(i);
                // if it's the same phrase
                if (oldPhrase.getId() == newPhrase.getId())
                    oldPhrase.update(newPhrase);
                    // else it's no longer exists, and there's a new
                else {
                    wordList.remove(i);
                    wordList.add(i, newPhrase);
                }
            }
        }


*/
    }
    private ArrayList<Integer> getOrderingArray(int sortType)
    {
        switch (sortType)
        {
            case SORT_DATE:
                return db.getOrderingArrayByDate(this);
            case SORT_ABC:
                return db.getOrderingArrayByABC(this);
            default:
                return db.getOrderingArrayByDate(this);
        }
    }
    public void addWordList(ArrayList<Word> wordList)
    {
        this.wordList.addAll(wordList);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public ArrayList<Word> getWordList() {
        return wordList;
    }

    public String getCode() {
        return code;
    }

    public int getNumOfWords() {
        return wordList.size();
    }

    public String getLanguage() {
        int i;
        for (i = 0; i < code.length() && code.charAt(i) != '_'; i++) {}
        return code.substring(0, i);
    }

    public String getTitle() {
        int i;
        for (i = 0; i < code.length() && code.charAt(i) != '_'; i++) {}
        if (i < code.length())
            return code.substring(i + 1).replace("__", ".").replace("_", " ");
        else
            return code;
    }

    public int getIndexOfPhrase(String phrase)
    {
        for (int i = 0; i < getNumOfWords(); i++)
            if (getWordList().get(i).getWord().equals(phrase))
                return i;
        return 0;
    }

/*
    public void makeDialogAddPhrase(final Context context)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View dialog_layout = inflater.inflate(R.layout.dialog, null);
        final EditText wordInput = (EditText) dialog_layout.findViewById(R.id.editText_word);
        final EditText meaningInput = (EditText) dialog_layout.findViewById(R.id.editText_meaning);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        wordInput.setText("");
        meaningInput.setText("");

        builder.setView(dialog_layout);
        builder.setTitle(R.string.dialog_add_phrase_title);
        builder.setPositiveButton(R.string.add, null);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wordInput.setText("");
                meaningInput.setText("");
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phrase = wordInput.getText().toString();
                final String meaning = meaningInput.getText().toString();

                switch (addPhrase(new Word(phrase, meaning)))
                {
                    case EMPTY:
                        Toast.makeText(context, "Fill both fields!", Toast.LENGTH_SHORT).show();
                        break;
                    case EXISTING:
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(context);

                        builder2.setMessage(R.string.dialog_append_phrase_title);
                        builder2.setNegativeButton("No", null);
                        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Word changedWord = getWord(phrase);
                                changedWord.appendMeaning(meaning);
                                changeWord(null, changedWord);

                                ActivityVocabulary.updateWordList();
                                ActivityVocabulary.listRefresh();
                            }
                        });
                        builder2.create().show();
                        dialog.dismiss();
                        break;
                    case NEW:
                        String info = "<font color='#FF4081'><b>" + phrase + "</b></font>";
                        Spanned infoText = Html.fromHtml(info + " has been added");
                        Toast.makeText(context, infoText, Toast.LENGTH_SHORT).show();
                        setWordList(mydb.getAllRecords(Vocabulary.this));
                        ActivityVocabulary.listRefresh();
                        dialog.dismiss();
                        break;
                    default:
                        dialog.dismiss();
                }
            }
        });


        if (filter != null && !filter.equals("")) {
            wordInput.setText(filter);
            wordInput.clearFocus();
            meaningInput.requestFocus();
        }
    }
*/
    public String getNameReference(Context context)
    {
        return  context.getString(
                context.getResources().getIdentifier(getLanguage(), "string", context.getPackageName()));
    }

    public int addPhrase(Word word)
    {
        if (word.isEmpty())
            return EMPTY;
        else
            if (db.insertWord(code, word, Vocabulary.this))
                return NEW;
            else
                return EXISTING;

    }
/*
    public boolean addWord(Word word, final Context context)
    {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View dialog_layout = inflater.inflate(R.layout.dialog, null);
        final EditText wordInput = (EditText) dialog_layout.findViewById(R.id.editText_word);
        final EditText meaningInput = (EditText) dialog_layout.findViewById(R.id.editText_meaning);
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        wordInput.setText("");
        meaningInput.setText("");

        builder.setView(dialog_layout);
        builder.setTitle("Adding a new word");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String phrase = wordInput.getText().toString();
                final String meaning = meaningInput.getText().toString();

                if (phrase.equals("") || meaning.equals("")) {   //if the user didn't fill all the fields
                    Toast.makeText(context, "Fill both fields!", Toast.LENGTH_SHORT).show();
                } else {   //if he did
                    dialog.dismiss();

                    if (mydb.insertWord(code, new Word(phrase, meaning), Vocabulary.this))
                    {
                        String info = "<font color='#FF4081'><b>" + phrase + "</b></font>";
                        Spanned infoText = Html.fromHtml(info + " has been added");
                        Toast.makeText(context, infoText, Toast.LENGTH_SHORT).show();
                        setWordList(mydb.getAllRecords(Vocabulary.this));
                        ActivityVocabulary.listRefresh();
                    }
                    else
                    {
                        final AlertDialog.Builder builder2 = new AlertDialog.Builder(context);

                        builder2.setMessage("Append word's meaning?");
                        builder2.setNegativeButton("No", null);
                        builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Word changedWord = getWord(phrase);
                                changedWord.appendMeaning(meaning);
                                changeWord(null, changedWord);

                                ActivityVocabulary.updateWordList();
                                ActivityVocabulary.listRefresh();
                            }
                        });

                        builder2.create().show();
                    }

                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                wordInput.setText("");
                meaningInput.setText("");
                dialog.dismiss();
            }
        });
        //TODO afte this getbutton - positive

        builder.create().show();
        if (filter != null && !filter.equals("")) {
            wordInput.setText(filter);
            wordInput.clearFocus();
            meaningInput.requestFocus();
        }
        return true;
    }
*/
    public void changeWord(Word oldWord, Word newWord)
    {
        db.updateWord(getCode(), newWord);
    }
/*
    public Drawable getFlag(Context context)
    {
        return context.getDrawable(Languages.getFlag(getLanguage()));
    }
*/

    public Word getWord(String phrase)
    {
        for (Word word : getWordList())
            if (word.getWord().toLowerCase().equals(phrase.toLowerCase()))
                return word;

        return null;
    }

    public boolean isContainer()
    {
        if (code.equals(getSubject()))
            return true;
        else
            return false;
    }

    public static final int REQUEST_WRITE_STORAGE = 112;

    File imported = null;
    public File makeTxt(Context context, Activity a)
    {
        boolean hasPermission = (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        } else {
            // You are allowed to write external storage:
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/new_folder";
            File storageDir = new File(path);
            if (!storageDir.exists() && !storageDir.mkdirs()) {
                // This should never happen - log handled exception!
            }
        }

/*
        File gpxfile = null;
        try {
            String location = "/mnt/sdcard/";
            File root = new File(location, "Vocabulary");
            if (!root.exists()) {
                root.mkdirs();
            }
            String fileName = code + ".txt";
            gpxfile = new File(root, fileName);
            //FileWriter writer = new FileWriter(gpxfile);
            //TODO encoding?  most ansi
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(gpxfile), "Cp1252"));

            writer.write(code);
            writer.newLine();
            writer.write(String.valueOf(wordList.size()));
            writer.newLine();

            for (Word word : wordList)
            {
                writer.write(word.getId() + " ; ");
                writer.write(word.getWord().replace("\n", " \\nl ") + " ; ");
                writer.write(word.getWord2().replace("\n", " \\nl "));
                writer.newLine();
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        this.context = context;
        try {
            new ExportPhrases().execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return imported;

    }

    private class ExportPhrases extends AsyncTask<Void, Void, Void>
    {
        ProgressDialog dialog = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            dialog.setMessage("Exporting phrases");
            dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(getNumOfWords());
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
                String fileName = code + ".txt";
                imported = new File(root, fileName);
                //FileWriter writer = new FileWriter(gpxfile);
                //TODO encoding?  most utf-8
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(imported)));

                writer.write(code);
                writer.newLine();
                writer.write(String.valueOf(wordList.size()));
                writer.newLine();

                for (Word word : wordList)
                {
                    writer.write(word.getId() + " ; ");
                    writer.write(word.getWord().replace("\n", " \\nl ") + " ; ");
                    writer.write(word.getWord2().replace("\n", " \\nl "));
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

                String info = "<font color='#e62e00'><b>" + getTitle() + "</b></font>";
                Spanned infoText = Html.fromHtml(info + " has been exported");
                Toast.makeText(context, infoText, Toast.LENGTH_SHORT).show();

                fresh();


            this.cancel(true);
        }

        protected void onCancelled() {
            dialog.dismiss();
            dialog = null;
        }
    }

    public int getSortType()
    {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_SORT, MODE_PRIVATE);
        return prefs.getInt(KEY_SORT, SORT_DATE);
    }
}
