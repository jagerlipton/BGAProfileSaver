package com.jagerlipton.bgaprofilesaver.presentation.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jagerlipton.bgaprofilesaver.R;
import com.jagerlipton.bgaprofilesaver.presentation.model.ArduinoProfileListUI;
import com.jagerlipton.bgaprofilesaver.presentation.util.ICommandToAdapter;
import com.jagerlipton.bgaprofilesaver.presentation.util.IValidEditTextListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ListAdapter extends RecyclerView.Adapter<ListHolder> {

    @NonNull
    private final ArrayList<ArduinoProfileListUI> inputArrayList = new ArrayList<>();
    private final Set<Integer> validationSet = new HashSet<>();

    //----------------------------------------------------------------------------------------------
    private IValidEditTextListener validEditTextListener;

    public void setValidValuesListener(IValidEditTextListener validEditTextListener) {
        this.validEditTextListener = validEditTextListener;
    }

    public void setValidValuesStatus(boolean flag) {
        if (validEditTextListener != null) validEditTextListener.isValid(flag);
    }
    //----------------------------------------------------------------------------------------------

    private final ICommandToAdapter commandToAdapterImpl = new ICommandToAdapter() {
        @Override
        public void saveItem(String value, int tag) {
            ArduinoProfileListUI input = new ArduinoProfileListUI();
            input = getListItem(tag);
            input.setValueName(value);
            setListItem(tag, input);
        }

        @Override
        public void validationBackgroundTint(EditText edittext, Context context) {
            Resources resources = context.getResources();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (TextUtils.isEmpty(edittext.getText())) {
                    edittext.setBackgroundTintList(resources.getColorStateList(R.color.error, context.getTheme()));
                } else {
                    edittext.setBackgroundTintList(resources.getColorStateList(R.color.ok, context.getTheme()));
                }
            }
        }

        @Override
        public void validationEditText(EditText edittext) {
            if (edittext.getTag() != null) {
                Integer position = (int) edittext.getTag();
                if (TextUtils.isEmpty(edittext.getText())) validationSet.add(position);
                else if (validationSet.contains(position))
                    validationSet.remove(position);
            }
        }

        @Override
        public void validationList() {
            if (validationSet.isEmpty()) setValidValuesStatus(true);
            else setValidValuesStatus(false);
        }
    };

    //----------------------------------------------------------------------------------------------

    public ListAdapter() {
    }


    @NonNull
    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_edittext_layout, parent, false);
        return new ListHolder(view, commandToAdapterImpl);
    }


    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
        holder.onBind(inputArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return inputArrayList.size();
    }

    public void addData(ArrayList<ArduinoProfileListUI> data) {
        inputArrayList.clear();
        inputArrayList.addAll(data);
        notifyDataSetChanged();
    }

    public ArduinoProfileListUI getListItem(int position) {
        return inputArrayList.get(position);
    }

    public void setListItem(int position, ArduinoProfileListUI inputDataUI) {
        if (inputArrayList.set(position, inputDataUI) != inputDataUI) {
            inputArrayList.set(position, inputDataUI);
        }
    }

    public ArrayList<ArduinoProfileListUI> getInputList() {
        return inputArrayList;
    }

}
