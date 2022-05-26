package com.jagerlipton.bgaprofilesaver.presentation.util;

import android.content.Context;
import android.widget.EditText;

public interface ICommandToAdapter {
    void saveItem(String value, int id);
    void validationBackgroundTint(EditText edittext, Context context);
    void validationEditText(EditText edittext);
    void validationList();
}
