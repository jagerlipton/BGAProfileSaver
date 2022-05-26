package com.jagerlipton.bgaprofilesaver.presentation.ui;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jagerlipton.bgaprofilesaver.R;
import com.jagerlipton.bgaprofilesaver.presentation.model.ArduinoProfileListUI;
import com.jagerlipton.bgaprofilesaver.presentation.util.CustomTextWatcher;
import com.jagerlipton.bgaprofilesaver.presentation.util.ICommandToAdapter;

public class ListHolder extends RecyclerView.ViewHolder {

    private final TextView idTextView;
    private final TextView keyTextView;
    private final EditText valueEditText;

    public ListHolder(View itemView, ICommandToAdapter listener) {
        super(itemView);
        idTextView = (TextView) itemView.findViewById(R.id.idTextView);
        keyTextView = (TextView) itemView.findViewById(R.id.keyTextView);
        valueEditText = (EditText) itemView.findViewById(R.id.valueEditText);
        valueEditText.addTextChangedListener(new CustomTextWatcher(valueEditText, listener));
    }

    public void onBind(ArduinoProfileListUI inputDataUI) {
        idTextView.setText(String.valueOf(inputDataUI.getId()));
        keyTextView.setText(inputDataUI.getKeyName());
        valueEditText.setTag(inputDataUI.getId() - 1);
        valueEditText.setText(inputDataUI.getValueName());
    }

}