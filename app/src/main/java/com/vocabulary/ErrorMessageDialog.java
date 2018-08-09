package com.vocabulary;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

class ErrorMessageDialog {

    ErrorMessageDialog(Context context, String message) {

        AlertDialog.Builder error = new AlertDialog.Builder(context);
        error.setTitle("Something went wrong!");
        error.setPositiveButton("OK", null);

        LayoutInflater inflater = error.create().getLayoutInflater();
        View errorView = inflater.inflate(R.layout.error, null);
        TextView errorMessage = (TextView) errorView.findViewById(R.id.message);

        error.setView(errorView);
        errorMessage.setText(message);

        AlertDialog dialog = error.create();
        dialog.show();
    }
}
