package com.madadgar.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.madadgar.MapsActivity;
import com.madadgar.R;
import com.madadgar.helper.Current;
import com.madadgar.objects.Request;
import com.madadgar.objects.ServiceProvider;
import com.squareup.picasso.Picasso;

public class SP_Home extends Fragment {

    private View view;
    private Switch serviceSwitch;
    private ProgressDialog progressDialog;
    private TextView description;
    private ImageView image;
    private Button startBtn;

    @Nullable
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_sp__home, container, false);

        description = view.findViewById(R.id.descriptionSP);
        image = view.findViewById(R.id.serviceImage);
        startBtn = view.findViewById(R.id.startServicee);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Current.databaseReference = null;
                serviceSwitch.setChecked(false);
                serviceSwitch.setEnabled(true);
                description.setVisibility(View.INVISIBLE);
                startBtn.setVisibility(View.INVISIBLE);
                image.setVisibility(View.INVISIBLE);
                startActivity(new Intent(view.getContext(), MapsActivity.class));


            }
        });

        TextView textView = (TextView)view.findViewById(R.id.hometitle);
        textView.setText("Welcome "+ Current.NAME);
        serviceSwitch = view.findViewById(R.id.sp_service);
        serviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    progressDialog = new ProgressDialog(view.getContext());
                    progressDialog.setMessage("Searching...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    // starting service..
                    // searching..
                    searching();

                }else{
                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }
                }

            }
        });


        return view;

    }

    private void searching(){

        FirebaseDatabase.getInstance()
                .getReference("users").child("serviceprovider").child(Current.KEY)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        final ServiceProvider provider = dataSnapshot.getValue(ServiceProvider.class);
                        if(!provider.emergencyRequests.isEmpty()){

                            final Request request = provider.emergencyRequests.get(0);
                            Current.request = request;
                            Toast.makeText(getActivity(), "Request Found", Toast.LENGTH_SHORT).show();
                            // downloading image.
                            StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                            storageReference = storageReference.child(Current.request.imageId);
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Picasso.with(view.getContext()).load(uri.toString()).into(image);
                                    description.setText("Description: "+request.description);
                                    ServiceProvider newProvider = provider;
                                    newProvider.emergencyRequests.clear();
                                    FirebaseDatabase.getInstance().getReference("users").child("serviceprovider")
                                            .child(provider.key).setValue(newProvider).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            progressDialog.dismiss();
                                            serviceSwitch.setEnabled(false);
                                            description.setVisibility(View.VISIBLE);
                                            startBtn.setVisibility(View.VISIBLE);
                                            image.setVisibility(View.VISIBLE);
                                            return;

                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Error in downloading image", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        serviceSwitch.setChecked(false);
        serviceSwitch.setEnabled(true);
        description.setVisibility(View.INVISIBLE);
        startBtn.setVisibility(View.INVISIBLE);
        image.setVisibility(View.INVISIBLE);

    }
}

