package com.jagerlipton.bgaprofilesaver;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class ListAdapter  extends RecyclerView.Adapter<ListHolder> {

    @NonNull
    public static ArrayList<InputClass> mInputArrayList = new ArrayList<>();
    public static Set validationSet = new HashSet();

    public ListAdapter() {
    }

    @Override
    public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.custom_edittext_layout, parent, false);
        return new ListHolder(view);
    }


    @Override
    public void onBindViewHolder(ListHolder holder, int position) {
         holder.mCounter.setText(mInputArrayList.get(position).getId().toString());
         holder.mKey.setText(mInputArrayList.get(position).getKeyName());
         holder.mValue.setTag(position);
         holder.mValue.setText(mInputArrayList.get(position).getValueName());
         //TODO сделать нормальный бинд в холдере, убрать паблики
    }

    @Override
    public int getItemCount() {
        return mInputArrayList.size();
    }

    public void addData(ArrayList<InputClass> data) {
       mInputArrayList.clear();
       mInputArrayList.addAll(data);
       notifyDataSetChanged();
    }



}
