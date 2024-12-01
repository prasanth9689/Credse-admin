package com.credse.credseadmin.adapter;

import static android.view.View.GONE;

import android.content.Context;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.credse.credseadmin.databinding.ModelVerifiedAadharListBinding;
import com.credse.credseadmin.model.VerifiedList;
import java.util.ArrayList;

public class VerifiedAadharAdapter extends RecyclerView.Adapter<VerifiedAadharAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<VerifiedList> arrayList;

    public VerifiedAadharAdapter(Context context, ArrayList<VerifiedList> arrayList){
        this.context =context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public VerifiedAadharAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ModelVerifiedAadharListBinding binding = ModelVerifiedAadharListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VerifiedAadharAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VerifiedAadharAdapter.ViewHolder holder, int position) {
        final VerifiedList verifiedList = arrayList.get(position);
        holder.binding.txtName.setText(verifiedList.getFirst_name());
        holder.binding.txtAadharNo.setText(verifiedList.getAadhaar_number());
        holder.binding.txtDob.setText(verifiedList.getDob());
        holder.binding.txtGender.setText(verifiedList.getGender());

        String fullAddress = verifiedList.getFatherName() + ", " + verifiedList.getDoorNo() + ", " +  verifiedList.getStreetName() + ", " + verifiedList.getStreetName() + ", "+ verifiedList.getPostOffice();
        holder.binding.txtAddress.setText(fullAddress);

        String staffName = verifiedList.getFirst_name();

        if (!staffName.isEmpty()) {
            holder.binding.firstLetter.setText(staffName.substring(0,1).toUpperCase());
            holder.binding.txtName.setText(staffName);
        }else {
            holder.binding.firstLetter.setText("E");
            holder.binding.txtName.setText("Error! No Name");
        }
        byte[] imageByteArray = Base64.decode(verifiedList.getPhoto(), Base64.DEFAULT);
        Glide.with(context)
                .load(imageByteArray)
                .into(holder.binding.photo);

        holder.binding.txtViewDetails.setOnClickListener(v -> {
            holder.binding.secondCn.setVisibility(View.VISIBLE);
            holder.binding.txtViewDetails.setVisibility(GONE);
            holder.binding.txtViewLess.setVisibility(View.VISIBLE);
        });

        holder.binding.txtViewLess.setOnClickListener(v -> {
            holder.binding.secondCn.setVisibility(View.GONE);
            holder.binding.txtViewDetails.setVisibility(View.VISIBLE);
            holder.binding.txtViewLess.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ModelVerifiedAadharListBinding binding;

        public ViewHolder (ModelVerifiedAadharListBinding binding){
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
