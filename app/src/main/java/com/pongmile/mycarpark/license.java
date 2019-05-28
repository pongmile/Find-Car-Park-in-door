package com.pongmile.mycarpark;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class license extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "WiFiDemo";
    public FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    SignInButton signIn;
    FirebaseAuth.AuthStateListener mAuthListener;
    public String u_email;
    EditText license_p;
    Button btn;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    String currentString = user.getEmail();
    String[] separated = currentString.split("\\.");

    DatabaseReference myRef = database.getReference("license").child(separated[0]);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.li_login);

        license_p = findViewById(R.id.edit_Li);

        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        currentString = user.getEmail();
        separated = currentString.split("\\.");

        myRef = database.getReference("license").child(separated[0]);


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                license_p.setText(value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });

        btn = findViewById(R.id.button_lic);
        btn.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.button_lic) {

            license_p = findViewById(R.id.edit_Li);
            String result = license_p.getText().toString();

            myRef.setValue(result);
            updateUI(user);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        myRef = database.getReference("license").child(separated[0]);
        currentString = user.getEmail();
        separated = currentString.split("\\.");
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        } else {

        }
    }


}
