package com.cscb07.museum;

import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.android.gms.tasks.OnSuccessListener;

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        Button buttonRecyclerView = view.findViewById(R.id.buttonRecyclerView);
        Button buttonScroller = view.findViewById(R.id.buttonScroller);
        Button buttonSpinner = view.findViewById(R.id.buttonSpinner);
        Button buttonManageItems = view.findViewById(R.id.buttonManageItems);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        
        // Hide the visibility by default for managing artifacts
        buttonManageItems.setVisibility(View.GONE);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        
        // Check the current user's role & allow them to see manage artifacts button IF they are an admin
        if(currentUser != null) {
        	DatabaseReference uReference = FirebaseDatabase.getInstance()
        		.getReference("users")
        		.child(currentUser.getUid());
        		
        	uReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>(){
        		@Override
        		public void onSuccess(DataSnapshot snap) {
        			User user = snap.getValue(User.class);
        			
        			if (user != null && user.checkAdmin() == true) {
        				buttonManageItems.setVisibility(View.VISIBLE);
        			}
        		}
        	});
        	
        }
        
        buttonRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new RecyclerViewFragment());
            }
        });

        buttonScroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ScrollerFragment());
            }
        });

        buttonSpinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SpinnerFragment());
            }
        });

        buttonManageItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { loadFragment(new ManageItemsFragment());}
        });

        // Returns the user to login screen after signing them out
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
                loadFragment(new LoginFragment());
            }
        });

        return view;
    }

    // Swaps displayed fragment
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
