package com.jagerlipton.bgaprofilesaver;

import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ListHolder extends RecyclerView.ViewHolder {

    public final TextView mCounter;
    public final TextView mKey;
    public final EditText mValue;

    public ListHolder(View itemView) {
        super(itemView);
        mCounter = (TextView) itemView.findViewById(R.id.textviewIterator);
        mKey = (TextView) itemView.findViewById(R.id.textviewKey);
        mValue = (EditText) itemView.findViewById(R.id.edittext);

        validationBackgroundTint(mValue, mValue.getContext());

        mValue.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                validationBackgroundTint(mValue, mValue.getContext());
                validationEditText();
                validationList();
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mValue.getTag() != null) {
                    InputClass input = new InputClass();
                    input = ListAdapter.mInputArrayList.get((int) mValue.getTag());
                    input.setValueName(mValue.getText().toString());
                    ListAdapter.mInputArrayList.set((int) mValue.getTag(), input);
                }
            }
        });


    }

    private static void validationBackgroundTint(EditText edittext, Context context) {
        Resources resources = context.getResources();

        if (TextUtils.isEmpty(edittext.getText())) {
            edittext.setBackgroundTintList(resources.getColorStateList(R.color.error, context.getTheme()));
        } else {
            edittext.setBackgroundTintList(resources.getColorStateList(R.color.ok, context.getTheme()));
        }
    }

    private void validationEditText() {
        if (mValue.getTag() != null) {
            Integer position = (int) mValue.getTag();
            if (TextUtils.isEmpty(mValue.getText())) ListAdapter.validationSet.add(position);
            else if (ListAdapter.validationSet.contains(position))
                ListAdapter.validationSet.remove(position);

        }
    }

    private void validationList() {
        if (ListAdapter.validationSet.isEmpty()) App.setValidValues(true);
        else App.setValidValues(false);
    }

}