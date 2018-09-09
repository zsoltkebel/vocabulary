package com.vocabulary.screens.main;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
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
import android.widget.AdapterView;

import com.vocabulary.JSONParser;
import com.vocabulary.R;
import com.vocabulary.realm.Vocabulary;
import com.vocabulary.screens.main.adapterVocabularies.AdapterVocabularies;

import butterknife.BindView;
import butterknife.ButterKnife;
import droidninja.filepicker.fragments.BaseFragment;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Kébel Zsolt on 2018. 03. 18..
 */

public class FragmentVocabularies extends BaseFragment {

    public static final String KEY_INDEX_OF_VOCABULARY = "vocabularyIndex";
    public static final String KEY_CODE_OF_VOCABULARY = "codeOfVocabulary";
    public static final String KEY_ACTIVITY = "activity";

    @BindView(R.id.clt_have_no_vocabularies)
    protected ConstraintLayout mCltNoVocabularies;

    private RecyclerView recyclerView;


    private Context context;

    private Vocabulary selectedVocabulary;
    private View root;

    public static String colorAccent;
    public static String colorTextGrey;

    ActivityMain activity;

    ProgressDialog progressDialog;

    AdapterVocabularies recyclerViewAdapter;

    Realm realm;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (ActivityMain) activity;
        realm = ((ActivityMain) activity).getRealm();
        //((ActivityMain) activity).setWindowColors(getResources().getColor(R.color.page2_dark));
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        realm = activity.getRealm();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_vocabularies, container, false);
        ButterKnife.bind(this, root);
        context = activity.getBaseContext();

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(getResources().getString(R.string.just_a_moment));
        progressDialog.setCancelable(false);

        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();

        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerview);


        recyclerViewAdapter = new AdapterVocabularies((ActivityMain) getActivity(), realm);

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
                    realm.where(Vocabulary.class).equalTo(Vocabulary.ID, activity.getSelectedVocabulary().getId()).findFirst();
                } catch (Exception e) {
                    realm = activity.getRealm();
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

    public void notifyRecyclerViewItemRangeChanged() {
        recyclerViewAdapter.notifyItemRangeChanged(0,
                activity.getVocabularies().size());
    }

    public View getRoot()
    {
        return root;
    }

    public ProgressDialog getProgressDialog() {
        return progressDialog;
    }


}
