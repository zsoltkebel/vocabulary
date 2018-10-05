package com.vocabulary.screens.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import android.text.Html;
import android.text.Spanned;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vocabulary.BuildConfig;
import com.vocabulary.JSONParser;
import com.vocabulary.R;
import com.vocabulary.realm.LearnOverview;
import com.vocabulary.realm.Phrase;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.learnconfig.ActivityLearnConfig;
import com.vocabulary.screens.main.adapterVocabularies.AdapterVocabularies;
import com.vocabulary.screens.merge.ActivityMerge;

import java.io.File;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import droidninja.filepicker.fragments.BaseFragment;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

import static com.vocabulary.screens.more.ActivityMore.SUCCESFUL;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class FragmentVocabularies extends BaseFragment {

    public static final String KEY_INDEX_OF_VOCABULARY = "vocabularyIndex";
    public static final String KEY_CODE_OF_VOCABULARY = "codeOfVocabulary";
    public static final String KEY_ACTIVITY = "mActivity";

    @BindView(R.id.clt_have_no_vocabularies)
    protected ConstraintLayout mCltNoVocabularies;

    @BindView(R.id.iv_mark_filter)
    protected ImageView mIvMarkFilter;

    private RecyclerView recyclerView;


    private Context context;

    private Vocabulary selectedVocabulary;
    private View root;

    public static String colorAccent;
    public static String colorTextGrey;

    ActivityMain mActivity;

    ProgressDialog progressDialog;

    AdapterVocabularies recyclerViewAdapter;

    Realm mRealm;

    AlertDialog mMoreDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (ActivityMain) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mRealm = mActivity.getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_vocabularies, container, false);
        ButterKnife.bind(this, root);
        context = mActivity.getBaseContext();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.just_a_moment));
        progressDialog.setCancelable(false);

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);


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
                    mRealm.where(Vocabulary.class).equalTo(Vocabulary.ID, mActivity.getSelectedVocabulary().getId()).findFirst();
                } catch (Exception e) {
                    mRealm = mActivity.getRealm();
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

    @Override
    protected int getFragmentLayout()
    {
        return R.layout.fragment_vocabularies;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

        colorAccent = "#" + getResources().getString(R.color.colorAccent).substring(3);
        colorTextGrey = "#" + getResources().getString(R.color.black).substring(3);

        selectedVocabulary = ((ActivityMain) getActivity()).getVocabularies().get(info.position);


        Spanned text = Html.fromHtml("<font color='" + colorTextGrey + "'>" + selectedVocabulary.getLanguageReference(getContext()) + " - </font>" + "<font color='" +
                colorAccent + "'>" + selectedVocabulary.getTitle() + "</font>");
        menu.setHeaderTitle(text);
        menu.setHeaderIcon(selectedVocabulary.getIconDrawable(getContext()));
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        super.onContextItemSelected(item);


        Spanned title = Html.fromHtml("<font color='" + colorTextGrey + "'>" + selectedVocabulary.getLanguageReference(context)
                + " - </font>" + "<font color='" + colorAccent
                + "'>" + selectedVocabulary.getTitle() + "</font>");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setNegativeButton(R.string.cancel, null);

        View dialog_layout;
        /*
        switch (item.getItemId()) {
            case R.id.learn:
                if (selectedVocabulary.getNumOfWords() > 0) {
                    Intent intent = add_new Intent(context, ActivityLearnConfig.class);
                    Bundle bundle = add_new Bundle();
                    bundle.putInt(KEY_INDEX_OF_VOCABULARY, indexOfVocabulary);
                    intent.putExtras(bundle);
                    startActivity(intent);
                } else {
                    Toast.makeText(context, "There isn't any word.", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.Delete_dictionary:

                dialog_layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_delete_vocabulary, null);
                builder.setView(dialog_layout);
                builder.setIcon(R.drawable.bin_icon);
                builder.setPositiveButton(R.string.btn_safe_delete, null);
                builder.setNegativeButton(R.string.btn_delete, null);
                builder.setNeutralButton(R.string.btn_cancel, null);
                final AlertDialog dialog = builder.create();
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(add_new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Data.deleteVocabulary(selectedVocabulary.getTitle(), selectedVocabulary.getLanguage());
                        listRefresh();
                        dialog.dismiss();
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(add_new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedVocabulary.makeTxt(context, getActivity()) != null)
                        {
                            Data.deleteVocabulary(selectedVocabulary.getTitle(), selectedVocabulary.getLanguage());
                            listRefresh();
                            dialog.dismiss();
                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            case R.id.Edit_dictionary_title:

                dialog_layout = getActivity().getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
                builder.setView(dialog_layout);
                builder.setIcon(R.drawable.edit_icon);
                builder.setPositiveButton(R.string.edit, null);
                final EditText editText = (EditText) dialog_layout.findViewById(R.id.editText_dic_name);
                editText.setText(selectedVocabulary.getTitle());
                editText.setSelection(editText.getText().length());
                final AlertDialog dialog_edit = builder.create();
                // Show keyboard
                dialog_edit.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE  | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog_edit.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                dialog_edit.show();
                //hideKeyboard(false);

                dialog_edit.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(add_new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Data.renameVocabulry(selectedVocabulary.getTitle(), selectedVocabulary.getLanguage(), editText.getText().toString())) {
                            listRefresh();//TODO fancy szoveg kiiras
                            dialog_edit.dismiss();
                            //hideKeyboard(true);
                            Toast.makeText(context, "Renamed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "This title has been already existing in this vocabularyCode",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog_edit.getButton(DialogInterface.BUTTON_NEGATIVE).setOnClickListener(add_new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog_edit.dismiss();
                        //hideKeyboard(true);
                    }
                });
                return true;
            case R.id.export_dictionary:
                getActivity().closeContextMenu();

                selectedVocabulary.makeTxt(context, getActivity());

                return true;
            case R.id.share_dictionary:
                getActivity().closeContextMenu();

                StrictMode.VmPolicy.Builder builder3 = add_new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder3.build());

                File file = selectedVocabulary.makeTxt(context, getActivity());
                String newFilePath = file.getAbsolutePath().replace(file.getName(), "") +
                        Normalizer.normalize(file.getName(), Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
                File newFile = add_new File(newFilePath);
                file.renameTo(newFile);

                Intent intent = add_new Intent(Intent.ACTION_SEND);
                intent.setType("text/*");
                String uriPath = "file://" + newFile.getAbsolutePath();

                intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(uriPath));
                startActivity(Intent.createChooser(intent, newFile.getName()));
                return true;
            default:
                return true;
                }
*/
        return true;

    }

    public void openContextMenu(View view)
    {
        recyclerView.showContextMenuForChild(view);
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
        ConstraintLayout ltRename = (ConstraintLayout) dialogView.findViewById(R.id.lt_click_rename);
        ConstraintLayout ltDelete = (ConstraintLayout) dialogView.findViewById(R.id.lt_click_delete);
        ConstraintLayout ltExport = (ConstraintLayout) dialogView.findViewById(R.id.lt_click_export);
        ConstraintLayout ltShare = (ConstraintLayout) dialogView.findViewById(R.id.lt_click_share);
        ConstraintLayout ltMerge = (ConstraintLayout) dialogView.findViewById(R.id.lt_click_merge);

        ltLearn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ActivityLearnConfig.class);
                intent.putExtra(Vocabulary.ID, mActivity.getSelectedVocabulary().getId());
                startActivity(intent);

                mMoreDialog.dismiss();
            }
        });

        ltRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                final View dialogView = mActivity.getLayoutInflater().inflate(R.layout.dialog_rename_vocabulary, null);
                final EditText titleEditText = (EditText) dialogView.findViewById(R.id.editText_dic_name);
                builder.setView(dialogView);
                titleEditText.setText(mActivity.getSelectedVocabulary().getTitle());
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
                                .equalTo(Vocabulary.LANGUAGE, mActivity.getSelectedVocabulary().getLanguage())
                                .equalTo(Vocabulary.TITLE, titleEditText.getText().toString())
                                .findFirst() == null) {
                            final String title = titleEditText.getText().toString();

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(titleEditText.getWindowToken(), 0);

                            mRealm.executeTransactionAsync(new Realm.Transaction() {
                                @Override
                                public void execute(Realm realm) {
                                    realm.where(Vocabulary.class)
                                            .equalTo(Vocabulary.ID, mActivity.getSelectedVocabulary().getId())
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
                        final String vocabularyId = mActivity.getSelectedVocabulary().getId();
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
                                                progressDialog.show();
                                                mRealm.executeTransactionAsync(new Realm.Transaction() {
                                                    @Override
                                                    public void execute(Realm realm) {
                                                        realm.copyToRealmOrUpdate(mActivity.getSelectedVocabulary());
                                                    }
                                                }, new Realm.Transaction.OnSuccess() {
                                                    @Override
                                                    public void onSuccess() {
                                                        progressDialog.dismiss();
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
                progressDialog.show();

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
                intent.putExtra(Vocabulary.ID, mActivity.getSelectedVocabulary().getId());
                startActivityForResult(intent, SUCCESFUL);

                mMoreDialog.dismiss();
            }
        });

    }

    public void showMoreDialog() {

        TextView tvLanguage = (TextView) dialogView.findViewById(R.id.tv_language);
        TextView tvTitle = (TextView) dialogView.findViewById(R.id.tv_title);
        TextView tvNumOfPhrases = (TextView) dialogView.findViewById(R.id.tv_num_of_phrases);
        TextView tvDate = (TextView) dialogView.findViewById(R.id.tv_date);
        ImageView ivIcon = (ImageView) dialogView.findViewById(R.id.iv_icon);

        tvLanguage.setText(mActivity.getSelectedVocabulary().getLanguage());
        tvTitle.setText(mActivity.getSelectedVocabulary().getTitle());
        tvNumOfPhrases.setText(String.valueOf(mActivity.getSelectedVocabulary().getPhrases().size()));
        tvDate.setText(mActivity.getSelectedVocabulary().getDateAndTime().substring(0, 10));

        ivIcon.setImageDrawable(mActivity.getSelectedVocabulary().getIconDrawable(getContext()));

        mMoreDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        mMoreDialog.show();
        progressDialog.dismiss();
    }

    public void notifyRecyclerViewItemRangeChanged() {
        recyclerViewAdapter.notifyItemRangeChanged(0,
                mActivity.getVocabularies().size());
    }

    public View getRoot()
    {
        return root;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }


}
