package com.vocabulary.screens.main;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.BuildConfig;
import com.vocabulary.JSONParser;
import com.vocabulary.R;
import com.vocabulary.realm.LearnOverview;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.RealmFragment;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.learnconfig.ActivityLearnConfig;
import com.vocabulary.screens.main.adapterVocabularies.AdapterVocabularies;
import com.vocabulary.screens.merge.ActivityMerge;

import java.io.File;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.vocabulary.screens.more.ActivityMore.SUCCESFUL;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class FragmentVocabularies extends RealmFragment {
    public static final String KEY_INDEX_OF_VOCABULARY = "vocabularyIndex";

    @BindView(R.id.clt_have_no_vocabularies) ConstraintLayout mCltNoVocabularies;
    @BindView(R.id.iv_mark_filter) ImageView mIvMarkFilter;
    @BindView(R.id.recyclerview) RecyclerView recyclerView;

    private Context context;

    public static String colorAccent;

    ActivityMain mActivityMain;

    AdapterVocabularies recyclerViewAdapter;

    AlertDialog mMoreDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vocabularies, container, false);
        ButterKnife.bind(this, root);
        mActivityMain = (ActivityMain) mActivity;
        context = mActivityMain.getBaseContext();

        recyclerViewAdapter = new AdapterVocabularies((ActivityMain) getActivity(), mRealm);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerViewAdapter);

        registerForContextMenu(recyclerView);

        if (((ActivityMain) getActivity()).getVocabularies().size() == JSONParser.getSubjects(context).size())
            mCltNoVocabularies.setVisibility(View.VISIBLE);
        else
            mCltNoVocabularies.setVisibility(View.GONE);

        ((ActivityMain) getActivity()).getVocabularies().addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<com.vocabulary.realm.Vocabulary>>() {
            @Override
            public void onChange(RealmResults<com.vocabulary.realm.Vocabulary> vocabularies, OrderedCollectionChangeSet changeSet) {
                try {
                    mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, mActivityMain.getSelectedVocabulary().getId()).findFirst();
                } catch (Exception e) {
                    mRealm = mActivityMain.getRealm();
                }

                if (vocabularies.size() == JSONParser.getSubjects(context).size())
                    mCltNoVocabularies.setVisibility(View.VISIBLE);
                else
                    mCltNoVocabularies.setVisibility(View.GONE);

                // `null`  means the async query returns the first time.
                if (changeSet == null) {
                    recyclerViewAdapter.notifyDataSetChanged();
                    return;
                }
                // For deletions, the adapter has to be notified in reverse order.
                OrderedCollectionChangeSet.Range[] deletions = changeSet.getDeletionRanges();
                for (int i = deletions.length - 1; i >= 0; i--) {
                    OrderedCollectionChangeSet.Range range = deletions[i];
                    recyclerViewAdapter.notifyItemRangeRemoved(range.startIndex, range.length);
                    recyclerViewAdapter.notifyDataSetChanged();
                }

                OrderedCollectionChangeSet.Range[] insertions = changeSet.getInsertionRanges();
                for (OrderedCollectionChangeSet.Range range : insertions) {
                    recyclerViewAdapter.notifyItemRangeInserted(range.startIndex, range.length);
                    recyclerViewAdapter.notifyItemRangeChanged(0, recyclerViewAdapter.getItemCount());
                    recyclerViewAdapter.notifyDataSetChanged();
                }

                OrderedCollectionChangeSet.Range[] modifications = changeSet.getChangeRanges();
                for (OrderedCollectionChangeSet.Range range : modifications) {
                    recyclerViewAdapter.notifyItemRangeChanged(range.startIndex, range.length);
                }
            }
        });

        createMoreDialog();

        return root;
    }

    @OnClick(R.id.iv_mark_filter)
    protected void onMarkFilterClicked() {
        recyclerViewAdapter.vibrate();
        if (mIvMarkFilter.isActivated()) {
            recyclerViewAdapter.changeShowOnlyMarked(false);
            mIvMarkFilter.setActivated(false);
        } else {
            recyclerViewAdapter.changeShowOnlyMarked(true);
            mIvMarkFilter.setActivated(true);
        }
    }
View dialogView;

    private void createMoreDialog() {
        final AlertDialog.Builder moreDialog = new AlertDialog.Builder(getContext());
        dialogView = LayoutInflater.from(getContext()).inflate(R.layout.activity_more, null);
        moreDialog.setView(dialogView);

        mMoreDialog = moreDialog.create();

        ConstraintLayout ltLearn = dialogView.findViewById(R.id.lt_click_learn);
        ConstraintLayout ltRename = dialogView.findViewById(R.id.lt_click_rename);
        ConstraintLayout ltDelete = dialogView.findViewById(R.id.lt_click_delete);
        ConstraintLayout ltExport = dialogView.findViewById(R.id.lt_click_export);
        ConstraintLayout ltShare = dialogView.findViewById(R.id.lt_click_share);
        ConstraintLayout ltMerge = dialogView.findViewById(R.id.lt_click_merge);

        ltLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityLearnConfig.class);
                intent.putExtra(Vocabulary.ID, mActivityMain.getSelectedVocabulary().getId());
                startActivity(intent);

                mMoreDialog.dismiss();
            }
        });

        ltRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                final View dialogView = mActivityMain.getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
                final EditText titleEditText = (EditText) dialogView.findViewById(R.id.editText_dic_name);
                builder.setView(dialogView);
                titleEditText.setText(mActivityMain.getSelectedVocabulary().getTitle());
                titleEditText.selectAll();

                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setPositiveButton(R.string.edit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mRealm.where(Vocabulary.class)
                                .equalTo(Vocabulary.LANGUAGE, mActivityMain.getSelectedVocabulary().getLanguage())
                                .equalTo(Vocabulary.TITLE, titleEditText.getText().toString())
                                .findFirst() == null) {
                            final String title = titleEditText.getText().toString();

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);

                            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(Vocabulary.class)
                                            .equalTo(Vocabulary.ID, mActivityMain.getSelectedVocabulary().getId())
                                            .findFirst()
                                            .setTitle(title);
                                }
                            }, new Realm.Transaction.OnSuccess() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(getContext(), R.string.msg_title_change_successful, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {

                        }
                    }
                });
                AlertDialog dialog = builder.create();

                dialog.show();
                titleEditText.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,InputMethodManager.HIDE_IMPLICIT_ONLY);

                mMoreDialog.dismiss();
            }
        });

        ltDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.are_you_sure_delete);
                builder.setNegativeButton(R.string.cancel, null);
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String vocabularyId = mActivityMain.getSelectedVocabulary().getId();
                        mRealm.executeTransactionAsync(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                RealmResults<Phrase> phrases = realm.where(Phrase.class).equalTo(Phrase.VOCABULARY_ID, vocabularyId).findAll();
                                phrases.deleteAllFromRealm();
                                Vocabulary vocabulary = realm.where(Vocabulary.class).equalTo(Vocabulary.ID, vocabularyId).findFirst();
                                vocabulary.deleteFromRealm();

                                if (realm.where(LearnOverview.class).equalTo(LearnOverview.ID, vocabularyId).findFirst() != null)
                                    realm.where(LearnOverview.class).equalTo(LearnOverview.ID, vocabularyId).findFirst().deleteFromRealm();
                            }
                        }, new Realm.Transaction.OnSuccess() {
                            @Override
                            public void onSuccess() {
                                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.coordinator_layout),
                                        getResources().getString(R.string.message_deleted), Snackbar.LENGTH_LONG)
                                        .setAction(R.string.undo, new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mActivityMain.showProgressDialog(true);
                                                mRealm.executeTransactionAsync(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        realm.copyToRealmOrUpdate(mActivityMain.getSelectedVocabulary());
                                                    }
                                                }, new Realm.Transaction.OnSuccess() {
                                                    @Override
                                                    public void onSuccess() {
                                                        mActivityMain.showProgressDialog(false);
                                                    }
                                                });
                                            }
                                        });
                                snackbar.show();
                            }
                        });
                    }
                });
                builder.show();
                mMoreDialog.dismiss();
            }
        });

        ltExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean hasPermission = (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
                if (!hasPermission) {
                    ActivityCompat.requestPermissions(getActivity(),
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
                    ExportPhrases exportPhrases = new ExportPhrases((ActivityMain) getActivity());
                    exportPhrases.execute().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                mMoreDialog.dismiss();
            }
        });

        ltShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivityMain.showProgressDialog(true);

                Uri uri;
                try {
                    ExportPhrases exportPhrases = new ExportPhrases((ActivityMain) getActivity());
                    exportPhrases.execute().get();
                    uri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", exportPhrases.getFile());

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/*");

                    intent.putExtra(Intent.EXTRA_STREAM, uri);
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.share)));

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                mMoreDialog.dismiss();
            }
        });

        ltMerge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityMerge.class);
                intent.putExtra(Vocabulary.ID, mActivityMain.getSelectedVocabulary().getId());
                startActivityForResult(intent, SUCCESFUL);

                mMoreDialog.dismiss();
            }
        });

    }

    public void showMoreDialog() {
        TextView tvLanguage = dialogView.findViewById(R.id.tv_language);
        TextView tvTitle = dialogView.findViewById(R.id.tv_title);
        TextView tvNumOfPhrases = dialogView.findViewById(R.id.tv_num_of_phrases);
        TextView tvDate = dialogView.findViewById(R.id.tv_date);
        ImageView ivIcon = dialogView.findViewById(R.id.iv_icon);

        tvLanguage.setText(mActivityMain.getSelectedVocabulary().getLanguage());
        tvTitle.setText(mActivityMain.getSelectedVocabulary().getTitle());
        tvNumOfPhrases.setText(String.valueOf(mActivityMain.getSelectedVocabulary().getPhrases().size()));
        tvDate.setText(mActivityMain.getSelectedVocabulary().getDateAndTime().substring(0, 10));

        ivIcon.setImageDrawable(mActivityMain.getSelectedVocabulary().getIconDrawable(getContext()));

        mMoreDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mMoreDialog.show();
        mActivityMain.showProgressDialog(false);
    }

}
