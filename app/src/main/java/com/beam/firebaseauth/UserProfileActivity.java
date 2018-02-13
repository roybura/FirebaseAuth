package com.beam.firebaseauth;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvUserEmail;
    private TextView tvUserName;
    private TextView tvUserPhoneNumber;
    private Button btnEditProfile;
    private Button btnLogout;
    private Button menuProfile;
    private Button menuSelectStore;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        setTitle("User Profile");

        initInstance();

        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        assert user != null;
        tvUserEmail.setText(String.format("Welcome %s", user.getEmail()));

        Query userQuery = databaseReference.child("User").child(user.getUid());
        userQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String userName = dataSnapshot.child("name").getValue(String.class);
                    String phoneNumber = dataSnapshot.child("phoneNumber").getValue(String.class);

                    tvUserName.setText(userName);
                    tvUserPhoneNumber.setText(phoneNumber);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                databaseError.getMessage();
            }
        });

        btnEditProfile.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
        menuProfile.setOnClickListener(this);
        menuSelectStore.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == btnEditProfile) {
            startActivity(new Intent(this, UserEditProfileActivity.class));
        }
        if (v == btnLogout) {
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }
        if (v == menuProfile) {
            finish();
            startActivity(new Intent(this, UserProfileActivity.class));
        }
        if (v == menuSelectStore) {
            finish();
            startActivity(new Intent(this, SelectStoreActivity.class));
        }
    }

    private void initInstance() {
        drawerLayout = findViewById(R.id.drawerLayout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(
                UserProfileActivity.this,
                drawerLayout,
                R.string.open_drawer,
                R.string.close_drawer
        );
        drawerLayout.addDrawerListener(actionBarDrawerToggle);

        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserPhoneNumber = findViewById(R.id.tvUserPhoneNumber);

        btnEditProfile = findViewById(R.id.btnEditProfile);
        btnLogout = findViewById(R.id.btnLogout);
        menuProfile = findViewById(R.id.menuProfile);
        menuSelectStore = findViewById(R.id.menuSelectStore);


        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}
