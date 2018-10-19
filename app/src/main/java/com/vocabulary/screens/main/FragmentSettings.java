package com.vocabulary.screens.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.vocabulary.R;
import com.vocabulary.realm.LearnOverview;
import com.vocabulary.realm.RealmFragment;
import com.vocabulary.screens.about.ActivityAbout;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;

public class FragmentSettings extends RealmFragment {
    final static String ENGLISH = "English";
    final static String HUNGARIAN = "Magyar";
    public static final String PREFS_SETTINGS = "settingsData";
    public static final String PREFS_LANGUAGE = "language";

    final String[] languages = new String[]{ENGLISH, HUNGARIAN};

    private ImageView ivClickBack;

    ActivityMain activity;

    private Spinner spinnerLanguage;
    private Button btnSave;
    private LinearLayout ltClickOpenAbout;

    private View root;

    int language;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, root);
        this.activity = (ActivityMain) getActivity();

        ivClickBack = (ImageView) root.findViewById(R.id.iv_click_back);

        ivClickBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        spinnerLanguage = (Spinner) root.findViewById(R.id.spinner_language);
        btnSave = (Button) root.findViewById(R.id.btn_save);
        ltClickOpenAbout = (LinearLayout) root.findViewById(R.id.lt_click_open_about);

//create a list of items for the spinner.

//create an adapter to describe how the items are displayed, adapters are used in several places in android.
//There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, languages);
//set the spinners adapter to the previously created one.
        spinnerLanguage.setAdapter(adapter);

        getPrefs();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPrefs();

                switch (language)
                {
                    case 0:
                        setLocale("en");
                        break;
                    case 1:
                        setLocale("hu");
                        break;
                }
            }
        });

        ltClickOpenAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityAbout.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @OnClick(R.id.iv_facebook)
    protected void onFacebookClicked() {
        String url = "https://www.facebook.com/MyVocabulary-418590168500849";

        Intent intent;

        try {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://page/" + "418590168500849"));
        } catch (Exception e) {
            intent =  new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/" + "418590168500849"));
        }

        startActivity(intent);
    }

    @OnClick(R.id.iv_gmail)
    protected void onGmailClicked() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        String[] strTo = { "kebel.zsolt@gmail.com" };

        //intent.putExtra(Intent.EXTRA_TEXT, "Body");
        intent.setType("message/rfc822");
        intent.setPackage("com.google.android.gm");
        intent.putExtra(Intent.EXTRA_EMAIL, strTo);
        intent.putExtra(Intent.EXTRA_SUBJECT, "MyVocabulary feedback");
        startActivity(intent);
    }

    private void getPrefs() {
        SharedPreferences prefs = activity.getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE);

        language = prefs.getInt(PREFS_LANGUAGE, 0); //0 is the default value.

        spinnerLanguage.setSelection(language);

    }

    private void setPrefs() {
        language = spinnerLanguage.getSelectedItemPosition();

        SharedPreferences.Editor editor = activity.getSharedPreferences(PREFS_SETTINGS, MODE_PRIVATE).edit();
        editor.putInt(PREFS_LANGUAGE, language);
        editor.apply();
    }
    Locale myLocale;

    public void setLocale(String lang) {
        if (lang == null)
        {
            myLocale = Locale.getDefault();
        }
        else
        {
            myLocale = new Locale(lang);
        }

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.locale = myLocale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        Intent refresh = new Intent(getContext(), ActivityMain.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        //ActivityMain.finishActivity();

        startActivity(refresh);
        getActivity().finish();
    }

    @OnClick(R.id.lt_click_clear_learn_history)
    protected void onClearLearnHistoryClicked() {

        ((ActivityMain) getActivity()).getRealm().beginTransaction();
        ((ActivityMain) getActivity()).getRealm().where(LearnOverview.class).findAll().deleteAllFromRealm();
        ((ActivityMain) getActivity()).getRealm().commitTransaction();
    }
}
