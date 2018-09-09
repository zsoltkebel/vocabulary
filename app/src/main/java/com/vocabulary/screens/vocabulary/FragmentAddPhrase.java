package com.vocabulary.screens.vocabulary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.R;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.utils.LanguageHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.fragments.BaseFragment;
import io.realm.Realm;

/**
 * Created by Kébel Zsolt on 2018. 03. 27..
 */

public class FragmentAddPhrase extends BaseFragment
{
    @BindView(R.id.et_phrase)
    protected EditText mEtPhrase;
    @BindView(R.id.et_meaning)
    protected EditText mEtMeaning;

    @BindView(R.id.v_update)
    protected View mViewUpdate;
    @BindView(R.id.v_state)
    protected View mViewState;

    @BindView(R.id.tv_existing_or_new)
    protected TextView mTvExistingOrNew;

    @BindView(R.id.lt_container)
    protected LinearLayout mLtContainer;
    @BindView(R.id.lt_phrase)
    protected LinearLayout mLtPhrase;
    @BindView(R.id.lt_move)
    protected ConstraintLayout mLtMove;
    @BindView(R.id.lt_update_existing)
    protected ConstraintLayout mLtUpdateExisting;

    View root;

    ConstraintLayout btnAddPhrase;

    Realm mRealm;
    Vocabulary mVocabulary;
    Phrase mPhrase;
    ActivityVocabulary activity;

    private String mPhraseString = "";
    private String mExplanation = "";

    boolean mAnimationInProcess = false;
    boolean mExistingVisible = true;
    boolean wasPhraseBefore = false;

    Handler handler = new Handler();

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_add_phrase;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mRealm = ((ActivityVocabulary) getActivity()).getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(getFragmentLayout(), container, false);
        ButterKnife.bind(this, root);

        activity = (ActivityVocabulary) getActivity();

        mVocabulary = ((ActivityVocabulary) getActivity()).getVocabulary();

        btnAddPhrase = (ConstraintLayout) root.findViewById(R.id.lt_click_add);

        mEtPhrase.requestFocus();
        hideKeyboard(false);

        btnAddPhrase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String p1 = mEtPhrase.getText().toString();
                final String p2 = mEtMeaning.getText().toString();

                if (p1.equals("") || p2.equals("")) {

                } else if (mPhrase != null) {
                    mRealm.beginTransaction();
                    mPhrase.setP2(mEtMeaning.getText().toString());
                    mRealm.commitTransaction();
                    Toast.makeText(getContext(), "Phrase updated", Toast.LENGTH_SHORT).show();

                    animateAddPhrase();
                } else {
                    final Phrase phrase = new Phrase();
                    phrase.set(p1, p2, mVocabulary);

                    final String vocabularyId = mVocabulary.getId();

                    mRealm.executeTransactionAsync(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            final Phrase managedPhrase = realm.copyToRealm(phrase);
                            realm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst().getPhrases().add(managedPhrase);
                        }
                    }, new Realm.Transaction.OnSuccess() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(getContext(), R.string.msg_added, Toast.LENGTH_SHORT).show();
                            mPhraseString = "";
                            mExplanation = "";
                            animateAddPhrase();
                        }
                    });

                    //activity.getFragmentPhrases().getAdapter().updateList(mRealm);

                }
            }
        });

        final RotateAnimation rotate = new RotateAnimation(
                0, 360,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        rotate.setDuration(1500);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());

        mEtPhrase.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                /*if (mPhrase != null) {
                    mEtMeaning.setText(mEtMeaning.getText().toString().replaceFirst(mPhrase.getP2(), "").trim());
                    while (mEtMeaning.getText().toString().startsWith(","))
                        mEtMeaning.setText(mEtMeaning.getText().toString().replaceFirst(",", "").trim());
                    while (mEtMeaning.getText().toString().endsWith(","))
                        mEtMeaning.setText(mEtMeaning.getText().toString().replaceFirst(",", "").trim());
                }*/
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPhraseString = s.toString();
                try {
                    handler.removeCallbacks(checkPhrase);
                } catch (Exception e) {}
                handler.postDelayed(checkPhrase, 200);
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*
                if (mPhrase != null) {
                    if (mEtMeaning.getText().toString().trim().equals(""))
                        mEtMeaning.setText(mPhrase.getP2());
                    else
                        mEtMeaning.setText(mPhrase.getP2() + ", " + mEtMeaning.getText().toString());
                }*/
            }
        });



        Animation rotation = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
        rotation.setFillAfter(true);


        return root;
    }

    Runnable checkPhrase = new Runnable() {
        @Override
        public void run() {
            mPhrase = mRealm.where(Phrase.class)
                    .equalTo(Phrase.VOCABULARY_ID, mVocabulary.getId())
                    .equalTo(Phrase.P1, mPhraseString).findFirst();
            if (mPhrase != null) {
                animateExistingIn();
                //mViewUpdate.setAnimation(rotate);
                mEtMeaning.setText(mPhrase.getP2());

                if (!wasPhraseBefore) {
                    mEtPhrase.setBackground(getResources().getDrawable(R.drawable.ripple_container_update_yellow_rounded));
                    mEtMeaning.setBackground(getResources().getDrawable(R.drawable.ripple_container_update_yellow_rounded));
                }
                wasPhraseBefore = true;

                switch (mPhrase.calculateState()) {
                    case Phrase.NEW:
                        mViewState.setBackground(getContext().getResources().getDrawable(R.drawable.new_dot_blue));
                        break;
                    case Phrase.DONT_KNOW:
                        mViewState.setBackground(getContext().getResources().getDrawable(R.drawable.new_dot_red));
                        break;
                    case Phrase.KINDA:
                        mViewState.setBackground(getContext().getResources().getDrawable(R.drawable.new_dot_yellow));
                        break;
                    case Phrase.KNOW:
                        mViewState.setBackground(getContext().getResources().getDrawable(R.drawable.new_dot_green));
                        break;
                }
            } else {
                mViewState.setBackground(getContext().getResources().getDrawable(R.drawable.new_dot_blue));

                if(!mAnimationInProcess && mExistingVisible)
                    animateExistingOut();

                mEtMeaning.setText("");

                if (wasPhraseBefore) {
                    mEtPhrase.setBackground(getResources().getDrawable(R.drawable.ripple_container_light_blue_rounded));
                    mEtMeaning.setBackground(getResources().getDrawable(R.drawable.ripple_container_light_blue_rounded));
                }

                wasPhraseBefore = false;
            }
        }
    };


    public void animateExistingIn() {
        mAnimationInProcess = true;
        mExistingVisible = true;
        //animate
        mLtMove.setTranslationY(-mLtUpdateExisting.getHeight());
        mLtMove.animate()
                .translationYBy(mLtUpdateExisting.getHeight())
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //mLtMove.setVisibility(View.VISIBLE);
                    }
                });
        mLtUpdateExisting.setTranslationY(-mLtUpdateExisting.getHeight());
        mLtUpdateExisting.setVisibility(View.VISIBLE);
        mLtUpdateExisting.animate()
                .translationYBy(mLtUpdateExisting.getHeight())
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //mLtUpdateExisting.setVisibility(View.VISIBLE);
                        mAnimationInProcess = false;
                    }
                });
    }

    public void animateExistingOut() {
        mAnimationInProcess = true;
        mExistingVisible = false;
        //animate
        mLtMove.animate()
                .translationYBy(-mLtUpdateExisting.getHeight())
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //mLtMove.setVisibility(View.VISIBLE);
                    }
                });
        mLtUpdateExisting.setVisibility(View.VISIBLE);
        mLtUpdateExisting.animate()
                .translationYBy(-mLtUpdateExisting.getHeight())
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        //mLtUpdateExisting.setVisibility(View.INVISIBLE);
                        mAnimationInProcess = false;
                    }
                });

    }

    public void animateAddPhrase() {
        mLtPhrase.animate()
                .translationXBy(-mLtContainer.getWidth())
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mEtPhrase.setText("");
                        mEtMeaning.setText("");

                        mEtPhrase.requestFocus();

                        mLtPhrase.setTranslationX(-mLtContainer.getWidth());
                        mLtPhrase.animate()
                                .translationXBy(mLtContainer.getWidth())
                                .setDuration(200)
                                .setInterpolator(new AccelerateDecelerateInterpolator())
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mLtMove.setVisibility(View.VISIBLE);
                                    }
                                });
                    }
                });
    }

    @OnClick(R.id.lt_google_translate)
    protected void onGoogleTranslateClicked() {
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setPackage("com.google.android.apps.translate");

            Uri uri = new Uri.Builder()
                    .scheme("http")
                    .authority("translate.google.com")
                    .path("/m/translate")
                    .appendQueryParameter("q", mEtPhrase.getText().toString())
                    .appendQueryParameter("tl", "hu") // target language
                    .appendQueryParameter("sl", LanguageHelper.getLanguageCode(mVocabulary.getLanguage())) // source language
                    .build();
            //intent.setType("text/plain"); //not needed, but possible
            intent.setData(uri);

            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity().getApplication(), "Sorry, Google Translate is not installed",
                    Toast.LENGTH_SHORT).show();
        }
    }



    private void hideKeyboard(boolean hide)
    {
        final InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        if (hide)
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        //else
           // inputMethodManager.showSoftInput(wordInput,InputMethodManager.SHOW_IMPLICIT);
    }

}
