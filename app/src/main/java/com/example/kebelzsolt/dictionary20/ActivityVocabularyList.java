package com.example.kebelzsolt.dictionary20;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kebelzsolt.dictionary20.main.vocabulary.ActivityVocabulary;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static com.example.kebelzsolt.dictionary20.Data.getIndexOfVocabulary;
import static com.example.kebelzsolt.dictionary20.Data.vocabularies;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.KEY_INDEX_OF_VOCABULARY;

public class ActivityVocabularyList extends AppCompatActivity {

    public static final String KEY_CODE_OF_VOCABULARY = "codeOfVocabulary";

    //ArrayList<Vocabulary> vocabularies;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dic_list);

        listView = (ListView) findViewById(R.id.lv_dicList);

        final DicListViewAdapter listViewAdapter = new DicListViewAdapter(this, vocabularies);
        listView.setAdapter(listViewAdapter);

        registerForContextMenu(listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle dataBundle = new Bundle();
                dataBundle.putString(KEY_CODE_OF_VOCABULARY, vocabularies.get(position).getCode());
                dataBundle.putInt(KEY_INDEX_OF_VOCABULARY, position);
                Intent intent;
                if (vocabularies.get(position).isContainer())
                    intent = new Intent(ActivityVocabularyList.this, ActivityVocabularyContainer.class);
                else
                    intent = new Intent(ActivityVocabularyList.this, ActivityVocabulary.class);
                intent.putExtras(dataBundle);
                //finish();
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (vocabularies.get(position).getCode().contains("_"))
                    return false;
                else
                    return true;
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        listRefresh();
    }
/*
    public void addDictionary(View view) {


        final View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_add_dic, null);
        final ListView languageList = (ListView) dialog_layout.findViewById(R.id.lv_flags);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LanguageMenuListViewAdapter adapter = new LanguageMenuListViewAdapter(ActivityVocabularyList.this);
        languageList.setAdapter(adapter);

        builder.setView(dialog_layout);
        builder.setTitle("Adding a new dictionary");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        final AlertDialog dialog = builder.create();
        dialog.show();


        languageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                dialog.dismiss();
                final AlertDialog dialog2;
                final View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_dic_name, null);
                //NEW_-----------------------------------------

                // Create the text field in the alert dialog...
                final ListView languageList = (ListView) dialog_layout.findViewById(R.id.lv_flags);

                // 1. Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder2 = new AlertDialog.Builder(ActivityVocabularyList.this);
                // 2. Chain together various setter methods to set the dialog characteristics
                builder2.setTitle("Set the title of your dictionary");
                builder2.setPositiveButton("Add", null);
                builder2.setNegativeButton("Cancel", null);
                //Set the layout
                builder2.setView(dialog_layout);

                // Create the AlertDialog
                dialog2 = builder2.create();

                dialog2.show();
                hideKeyboard(false);
                final EditText dicName = (EditText) dialog2.findViewById(R.id.editText_dic_name);
                dialog2.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = dicName.getText().toString();
                        if (!Data.addVocabuary(name, Languages.getLanguage(position)))
                        {
                            Toast.makeText(ActivityVocabularyList.this, "A dictionary has been already existing in the same language",
                                    Toast.LENGTH_SHORT).show();
                            dicName.setText("");
                        }
                        else
                        {
                            Toast.makeText(ActivityVocabularyList.this, "The dictionary has been added",
                                    Toast.LENGTH_SHORT).show();
                            hideKeyboard(true);
                            dialog2.dismiss();
                            listRefresh();
                        }
                    }
                });
            }
        });
    }
*/
    private void hideKeyboard(boolean hide)
    {
        final InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(this.INPUT_METHOD_SERVICE);
        if (hide)
            inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        else
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    public void listRefresh()
    {
        Parcelable state = listView.onSaveInstanceState();

        Data.updateVocabularies();
        DicListViewAdapter listViewAdapter = new DicListViewAdapter(this, vocabularies);
        listView.setAdapter(listViewAdapter);

        listView.onRestoreInstanceState(state);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVocabularyList.this);
        builder.setTitle("Did you learn all the words?");
        builder.setPositiveButton("Yes", null);
        builder.setNegativeButton("No", null);

        dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                finish();
                System.exit(0);
            }
        });
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }

    Vocabulary current;

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;


        current = vocabularies.get(info.position);


        Spanned text = Html.fromHtml("<font color='#595959'>" + current.getNameReference(this) + " - </font>" + "<font color='#FF4081'>" + current.getTitle() + "</font>");
        menu.setHeaderTitle(text);
        menu.setHeaderIcon(current.getIconDrawable(getBaseContext()));
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        super.onContextItemSelected(item);

        Spanned title = Html.fromHtml("<font color='#4D808080'>" + current.getNameReference(this) + " - </font>" + "<font color='#FF4081'>" + current.getTitle() + "</font>");

        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityVocabularyList.this);
        builder.setTitle(title);
        builder.setNegativeButton("Cancel", null);

        switch (item.getItemId()) {
            case R.id.Delete_dictionary:

                builder.setMessage("Are you sure want to delete it?");
                builder.setPositiveButton("Yes", null);
                final AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Data.deleteVocabulary(current.getTitle(), current.getLanguage());
                        listRefresh();
                        dialog.dismiss();
                        Toast.makeText(ActivityVocabularyList.this, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                return true;
            case R.id.Edit_dictionary_title:

                final View dialog_layout = getLayoutInflater().inflate(R.layout.dialog_dic_name, null);
                builder.setView(dialog_layout);
                builder.setPositiveButton("Edit", null);
                builder.setNegativeButton("Cancel", null);
                final EditText editText = (EditText) dialog_layout.findViewById(R.id.editText_dic_name);
                editText.setText(current.getTitle());
                editText.setSelection(editText.getText().length());
                final AlertDialog dialog_edit = builder.create();
                dialog_edit.show();
                hideKeyboard(false);

                dialog_edit.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Data.renameVocabulry(current.getTitle(), current.getLanguage(), editText.getText().toString())) {
                            listRefresh();//TODO fancy szoveg kiiras
                            dialog_edit.dismiss();
                            hideKeyboard(true);
                            Toast.makeText(ActivityVocabularyList.this, "Renamed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ActivityVocabularyList.this, "This title has been already existing in this vocabularyCode",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog_edit.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_edit.dismiss();
                        hideKeyboard(true);
                    }
                });
                return true;
            case R.id.export_dictionary:
                current.makeTxt(this, this);
                return true;
            default:
                return true;

        }

    }


    public void importVocabulary(View view)
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null)
            return;
        try {
            uri = data.getData();
            inputStream = getContentResolver().openInputStream(uri);
            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();

            if (!Data.isSubject(line))
            {
                Toast.makeText(this, "Wrong format in txt", Toast.LENGTH_SHORT).show();
                return;
            }
            importedVocabulary = new Vocabulary(line);

            if (Data.addVocabuary(line))
                Data.updateVocabularies();

            try {
                numOfPhrases = Integer.valueOf(reader.readLine());
            } catch (Exception exception) {
                Toast.makeText(this, "Wrong format in txt", Toast.LENGTH_SHORT).show();
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
        ProgressDialog dialog = new ProgressDialog(ActivityVocabularyList.this);
        private int numOfNewPhrases = 0;

        @Override
        protected void onPreExecute() {
            //set message of the dialog
            dialog.setMessage("Importing phrases");
            dialog.setProgressStyle(dialog.STYLE_HORIZONTAL);
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
                String info = "<font color='#FF4081'><b>" + numOfNewPhrases + "</b></font>";
                Spanned infoText = Html.fromHtml(info + " new phrase has been imported");
                Toast.makeText(ActivityVocabularyList.this, infoText, Toast.LENGTH_SHORT).show();
                listRefresh();
            }
            else
            {
                Toast.makeText(ActivityVocabularyList.this, "There's no new phrase", Toast.LENGTH_SHORT).show();
            }

            this.cancel(true);
        }

        protected void onCancelled() {
            dialog.dismiss();
            dialog = null;
        }
    }
}


