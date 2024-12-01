package com.credse.credseadmin.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.credse.credseadmin.databinding.ModelStaffListBinding;
import com.credse.credseadmin.model.Staff;

import java.util.ArrayList;

public class StaffAdapater extends RecyclerView.Adapter<StaffAdapater.ViewHolder> {
    private final Context context;
    private final ArrayList<Staff> arrayList;

    public StaffAdapater(Context context, ArrayList<Staff> arrayList){
        this.context =context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelStaffListBinding binding = ModelStaffListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Staff staff = arrayList.get(position);
        holder.binding.txtName.setText(staff.getName());
        holder.binding.txtMobile.setText(staff.getMobile());

        String staffName = staff.getName();

        if (!staffName.isEmpty()) {
            holder.binding.firstLetter.setText(staffName.substring(0,1).toUpperCase());
            holder.binding.txtName.setText(staffName);
        }else {
            holder.binding.firstLetter.setText("E");
            holder.binding.txtName.setText("Error! No Name");
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ModelStaffListBinding binding;

        public ViewHolder (ModelStaffListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
