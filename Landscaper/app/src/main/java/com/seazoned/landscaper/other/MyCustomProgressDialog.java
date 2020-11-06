package com.seazoned.landscaper.other;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.seazoned.landscaper.R;


public class MyCustomProgressDialog extends ProgressDialog {


  public static ProgressDialog ctor(Context context) {
    MyCustomProgressDialog dialog = new MyCustomProgressDialog(context, R.style.MyTheme);
      //CircleProgressBar circleProgressBar=new CircleProgressBar(context);
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
      //circleProgressBar.setShowArrow(true);
      //circleProgressBar.setCircleBackgroundEnabled(false);
    return dialog;
  }

  public MyCustomProgressDialog(Context context) {
    super(context);
  }

  public MyCustomProgressDialog(Context context, int theme) {
    super(context, theme);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_custom_progress_dialog);



 /*  ImageView la = (ImageView) findViewById(R.id.animation);
    la.setBackgroundResource(R.drawable.custom_progress_dialog_animation);
    animation = (AnimationDrawable) la.getBackground();*/
  }

  /*@Override
  public void show() {
    super.show();
    animation.start();
  }

  @Override
  public void dismiss() {
    super.dismiss();
    animation.stop();
  }*/
}
