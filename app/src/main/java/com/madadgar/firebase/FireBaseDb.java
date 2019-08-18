package com.madadgar.firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.madadgar._interface.FirebaseOperations;
import com.madadgar._interface.FirebaseResponse;
import com.madadgar.model.Emergency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class required to perform Firebase Database Operations
 * insert
 * delete
 * update
 * edit
 */
public class FireBaseDb implements FirebaseOperations, DatabaseReference.CompletionListener {

    private FirebaseResponse firebaseResponse;

    public FireBaseDb(FirebaseResponse firebaseResponse) {
        this.firebaseResponse = firebaseResponse;
    }

    @Override
    public boolean insert(DatabaseReference databaseReference, String key, Object value) {
        databaseReference.child(key).setValue(value, this);
        return true;
    }

    @Override
    public boolean delete(DatabaseReference databaseReference, String key, Object value) {
        databaseReference.child(key).removeValue(this);
        return false;
    }

    @Override
    public boolean update(DatabaseReference databaseReference, String key, Object value) {
        databaseReference.child(key).setValue(value, this);
        return true;
    }

    @Override
    public List view(final Query query, final Class refClass) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Object> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Object object = postSnapshot.getValue(refClass);
                    list.add(object);
                }
                firebaseResponse.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                firebaseResponse.onFailure(databaseError.getMessage());
            }
        });
        return null;
    }

    public List view(final Query query, final Class refClass, final FirebaseResponse response) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Object> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Object object = postSnapshot.getValue(refClass);
                    list.add(object);
                }
                response.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                response.onFailure(databaseError.getMessage());
            }
        });
        return null;
    }

    public List viewAll(final Query query, final Class refClass, final FirebaseResponse response) {
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Object> list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    list.add(((HashMap) ((HashMap) dataSnapshot.getValue()).values().toArray()[0]).values());
                    response.onSuccess(list);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                response.onFailure(databaseError.getMessage());
            }
        });
        return null;
    }

    @Override
    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
    }
}
