package com.jagerlipton.bgaprofileeditor.presentation.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CustomTextWatcher implements TextWatcher {
    private final EditText editText;
    private final ICommandToAdapter commandToAdapter;

    public CustomTextWatcher(EditText editText, ICommandToAdapter commandToAdapter) {
        this.editText = editText;
        this.commandToAdapter = commandToAdapter;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        commandToAdapter.saveItem(editText.getText().toString(), (int) editText.getTag());
        commandToAdapter.validationBackgroundTint(editText, editText.getContext());
        commandToAdapter.validationEditText(editText);
        commandToAdapter.validationList();
    }
}
