package com.ahmed.martin.wassal_delivery;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class sign_in extends AppCompatActivity {


    TextView email ,password;

    FirebaseAuth mauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mauth= FirebaseAuth.getInstance();
        email = findViewById(R.id.sign_in_email);
        password = findViewById(R.id.sign_in_password);


    }
    DatabaseReference d_ref;
    ValueEventListener d_listen;
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mauth.getCurrentUser();

        d_listen = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    order_data order = dataSnapshot.getValue(order_data.class);
                    Intent details = new Intent(sign_in.this, order_details.class);
                    details.putExtra("order", order);
                    details.putExtra("have_order", true);
                    startActivity(details);
                    finish();
                } else {
                    order_data order_details = new order_data();
                    Intent main = new Intent(sign_in.this,MainActivity.class);
                    main.putExtra("order", order_details);
                    startActivity(main);
                    finish();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        if(currentUser!=null) {
            d_ref = FirebaseDatabase.getInstance().getReference().child("delivery").child(currentUser.getUid()).child("order");
            d_ref.addValueEventListener(d_listen);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(d_ref!=null)
            d_ref.removeEventListener(d_listen);
    }


    // if user forget his password
    public void forget_password(View view) {
        String Email = email.getText().toString();
        // check if email is empty
        if(TextUtils.isEmpty(Email))
            email.setError("please enter your email !!");
        else{
            // reset your password
            mauth.sendPasswordResetEmail(Email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(Task<Void> task) {
                    // if sent email to reset show message (check your email to reset password)
                    if(task.isSuccessful())
                        Toast.makeText(sign_in.this, "check your email to reset password", Toast.LENGTH_SHORT).show();
                        // if have error
                    else
                        Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // sign in with email and password
    public void sign_in(View view) {
        String Email = email.getText().toString();
        String Password = password.getText().toString();
        //check if password is empty or not
        if(TextUtils.isEmpty(Email))
            email.setError("please enter your email !!");
        else{
            // check if password is empty or not
            if(TextUtils.isEmpty(Password))
                password.setError("please enter your password !!");
            else {
                // sign in with email and password
                mauth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        // if signed in start main activity
                        if (task.isSuccessful()) {
                            FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
                            d_ref = FirebaseDatabase.getInstance().getReference().child("delivery").child(curUser.getUid()).child("order");
                            d_ref.addValueEventListener(d_listen);

                            // if not show error message
                        }else
                            Toast.makeText(sign_in.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
            }
        }
    }

    public void sign_up(View view) {
        startActivity(new Intent(sign_in.this,sign_up.class));
    }
}
