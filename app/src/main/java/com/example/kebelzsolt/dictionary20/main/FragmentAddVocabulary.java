package com.example.kebelzsolt.dictionary20.main;

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
import android.widget.ListView;
import android.widget.Toast;

import com.example.kebelzsolt.dictionary20.Data;
import com.example.kebelzsolt.dictionary20.LanguageMenuListViewAdapter;
import com.example.kebelzsolt.dictionary20.R;

import droidninja.filepicker.fragments.BaseFragment;

import static com.example.kebelzsolt.dictionary20.Data.subjects;
import static com.example.kebelzsolt.dictionary20.main.ActivityTabLayout.fresh;
import static com.example.kebelzsolt.dictionary20.main.FragmentVocabularyList.listRefresh;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class FragmentAddVocabulary extends BaseFragment
{
    View root;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_page_add;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        root = inflater.inflate(R.layout.fragment_page_add, container, false);



        final ListView languageList = (ListView) root.findViewById(R.id.lv_flags);
        LanguageMenuListViewAdapter adapter = new LanguageMenuListViewAdapter(getContext());
        languageList.setAdapter(adapter);

        languageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog dialog;
                final View dialog_layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
                //NEW_-----------------------------------------

                // 1. Instantiate an AlertDialog.Builder with its constructor
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle(R.string.set_title);
                builder.setIcon(subjects.get(position).getIconDrawable(getContext()));
                builder.setPositiveButton(R.string.add, null);
                builder.setNegativeButton(R.string.btn_cancel, null);
                //Set the layout
                builder.setView(dialog_layout);

                // Create the AlertDialog
                dialog = builder.create();

                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

                dialog.show();

                final EditText dicName = (EditText) dialog.findViewById(R.id.editText_dic_name);
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String name = dicName.getText().toString();
                        if (!Data.addVocabuary(name, subjects.get(position).getSubject()))
                        {
                            Toast.makeText(getContext(), "A dictionary has been already existing in the same language",
                                    Toast.LENGTH_SHORT).show();
                            dicName.setText("");
                        }
                        else
                        {
                            Toast.makeText(getContext(), "The dictionary has been added",
                                    Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            listRefresh();
                            fresh();
                        }
                    }
                });
            }
        });

        return root;
    }

    public void onResume() {
        super.onResume();
    }

    public View getRoot()
    {
        return root;
    }

    private void hideKeyboard(boolean hide, View view)
    {
        final InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (hide)
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        else
            inputMethodManager.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
    }
}
