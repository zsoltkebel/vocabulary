// Generated code from Butter Knife. Do not modify!
package com.example.kebelzsolt.dictionary20.main.vocabulary;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.example.kebelzsolt.dictionary20.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class ActivityVocabulary_ViewBinding implements Unbinder {
  private ActivityVocabulary target;

  @UiThread
  public ActivityVocabulary_ViewBinding(ActivityVocabulary target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public ActivityVocabulary_ViewBinding(ActivityVocabulary target, View source) {
    this.target = target;

    target.clearEditText = Utils.findRequiredViewAsType(source, R.id.imgvClear, "field 'clearEditText'", ImageView.class);
    target.icon = Utils.findRequiredViewAsType(source, R.id.imageView_wordList_flag, "field 'icon'", ImageView.class);
    target.txtv_name = Utils.findRequiredViewAsType(source, R.id.txt_language, "field 'txtv_name'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    ActivityVocabulary target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.clearEditText = null;
    target.icon = null;
    target.txtv_name = null;
  }
}
