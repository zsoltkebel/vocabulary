// Generated code from Butter Knife. Do not modify!
package com.example.kebelzsolt.dictionary20.main.vocabulary;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.example.kebelzsolt.dictionary20.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class FragmentPhraseList_ViewBinding implements Unbinder {
  private FragmentPhraseList target;

  private View view2131689646;

  private View view2131689645;

  @UiThread
  public FragmentPhraseList_ViewBinding(final FragmentPhraseList target, View source) {
    this.target = target;

    View view;
    target.recyclerView = Utils.findRequiredViewAsType(source, R.id.rv_list, "field 'recyclerView'", RecyclerView.class);
    target.tv_numberOfSelected = Utils.findRequiredViewAsType(source, R.id.tv_numOfSelected, "field 'tv_numberOfSelected'", TextView.class);
    target.options = Utils.findRequiredViewAsType(source, R.id.layout_options, "field 'options'", LinearLayout.class);
    view = Utils.findRequiredView(source, R.id.btn_delSelected, "field 'deleteSelected' and method 'delete'");
    target.deleteSelected = Utils.castView(view, R.id.btn_delSelected, "field 'deleteSelected'", ImageView.class);
    view2131689646 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.delete();
      }
    });
    view = Utils.findRequiredView(source, R.id.btn_edit, "field 'editSelected' and method 'editWord'");
    target.editSelected = Utils.castView(view, R.id.btn_edit, "field 'editSelected'", ImageView.class);
    view2131689645 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.editWord();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    FragmentPhraseList target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recyclerView = null;
    target.tv_numberOfSelected = null;
    target.options = null;
    target.deleteSelected = null;
    target.editSelected = null;

    view2131689646.setOnClickListener(null);
    view2131689646 = null;
    view2131689645.setOnClickListener(null);
    view2131689645 = null;
  }
}
