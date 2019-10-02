package com.madadgar.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.madadgar.R;
import com.madadgar._interface.ItemClickListener;
import com.madadgar.enums.EmergencyStatus;
import com.madadgar.model.Blood;
import com.madadgar.model.Emergency;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * RecyclerView Holder for RecyclerView Adapter required to bind data of every item in list to its view
 */
public class RecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
    private ItemClickListener itemClickListener;
    private List anyList;
    private View itemView;

    public <Model> RecyclerViewHolder(View view, ItemClickListener itemClickListener, List<Model> anyList) {
        super(view);
        view.setOnClickListener(this);
        view.setOnLongClickListener(this);
        this.itemClickListener = itemClickListener;
        this.anyList = anyList;
        this.itemView = view;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onItemClicked(anyList.get(getAdapterPosition()));
    }

    public <Model> void bind(Model model) {
        if (model instanceof Emergency) {
            ImageView imageView_emergencyImage = itemView.findViewById(R.id.imageView_emergencyPhoto);
            TextView textView_emergencyType = itemView.findViewById(R.id.textView_emergencyType);
            TextView textView_emergencyLocation = itemView.findViewById(R.id.textView_emergencyLocation);
            TextView textView_emergencyReportingTime = itemView.findViewById(R.id.textView_emergencyReportingTime);
            TextView textView_emergencyStatus = itemView.findViewById(R.id.textView_emergencyStatus);

            textView_emergencyType.setText(((Emergency) model).getEmergencyType());
            textView_emergencyLocation.setText(((Emergency) model).getEmergencyLocationAddress());

            String reportingTime = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa").format(((Emergency) model).getEmergencyReportingTime());
            textView_emergencyReportingTime.setText(reportingTime);
            Glide.with(itemView.getContext()).load(((Emergency) model).getEmergencyPhotoUrl()).into(imageView_emergencyImage);

            textView_emergencyStatus.setText(((Emergency) model).getEmergencyStatus().toString());
            if (((Emergency) model).getEmergencyStatus() == EmergencyStatus.COMPLETED) {
                textView_emergencyStatus.setTextColor(itemView.getContext().getColor(R.color.colorPrimary));
            }
        } else if (model instanceof Blood) {
            Blood blood = (Blood) model;

            TextView textView_userName = itemView.findViewById(R.id.textView_userName);
            TextView textView_bloodRequestDate = itemView.findViewById(R.id.textView_bloodRequestDate);
            TextView textView_bloodRequestLocation = itemView.findViewById(R.id.textView_bloodRequestLocation);
            TextView textView_bloodQuantity = itemView.findViewById(R.id.textView_bloodQuantity);

            textView_userName.setText(blood.getUserName());
            String requestDate = new SimpleDateFormat("dd MMM yyyy hh:mm:ss aa").format(blood.getBloodRequestDate());
            textView_bloodRequestDate.setText(requestDate);
            textView_bloodRequestLocation.setText(blood.getBloodRequestLocation());
            textView_bloodQuantity.setText(blood.getBloodRequestType() + " : " + blood.getQuantity() + " Bottle(s)");

        }
    }

    /**
     * Returns object with view whenever user make a long click on item from List(RecyclerView)
     */
    @Override
    public boolean onLongClick(View view) {
        itemClickListener.onItemLongClicked(view, anyList.get(getAdapterPosition()));
        return true;
    }
}
