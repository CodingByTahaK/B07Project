package com.cscb07.museum;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.tasks.OnSuccessListener;

public class ManageItemsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_items, container, false);

        Button buttonAddItem = view.findViewById(R.id.buttonAddItem);
        Button buttonDeleteItem = view.findViewById(R.id.buttonDeleteItem);
        Button buttonSavedItems = view.findViewById(R.id.buttonSavedItems);
        Button buttonBack = view.findViewById(R.id.buttonBack);

        buttonAddItem.setVisibility(View.GONE);
        buttonDeleteItem.setVisibility(View.GONE);

        buttonSavedItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SavedItemsFragment());
            }
        });

        FirebaseUser uCurrent = FirebaseAuth.getInstance().getCurrentUser();
        if (uCurrent != null) {
            DatabaseReference uReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uCurrent.getUid());
            uReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>(){
                @Override
                public void onSuccess(DataSnapshot snap) {
                    User user = snap.getValue(User.class);
                    if (user != null && user.checkAdmin() == true) {
                        buttonAddItem.setVisibility(View.VISIBLE);
                        buttonDeleteItem.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        buttonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddItemFragment());
            }
        });

        buttonDeleteItem.setOnClickListener(v -> loadFragment(new DeleteItemFragment()));

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
